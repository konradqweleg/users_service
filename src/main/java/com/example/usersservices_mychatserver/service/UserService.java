package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.*;
import com.example.usersservices_mychatserver.exception.UnexpectedInternalException;
import com.example.usersservices_mychatserver.exception.activation.BadActiveAccountCodeException;
import com.example.usersservices_mychatserver.exception.activation.ActivationCodeNotFoundException;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.exception.SendVerificationCodeException;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class UserService implements UserPort {
    private final UserRepositoryPort userRepositoryPort;
    private final SendEmailToUserPort sendEmail;
    private final GenerateRandomCodePort generateRandomCodePort;
    private final UserAuthPort userAuthPort;
    private static final Logger logger = LogManager.getLogger(UserService.class);


    public UserService(UserRepositoryPort userRepositoryPort, SendEmailToUserPort sendEmail, GenerateRandomCodePort generateRandomCodePort, UserAuthPort userAuthPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.sendEmail = sendEmail;
        this.generateRandomCodePort = generateRandomCodePort;
        this.userAuthPort = userAuthPort;
    }

    @Override
    public Mono<Result<Status>> changeUserPassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono) {
        return userEmailAndCodeAndPasswordMono.flatMap(userEmailCodeAndPassword ->
                userRepositoryPort.findUserWithEmail(userEmailCodeAndPassword.email())
                        .flatMap(userFromDb -> {
                            logger.info("User found with email: {}", userFromDb.email());
                            return userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))
                                    .flatMap(code -> {
                                        if (code.code().equals(userEmailCodeAndPassword.code())) {
                                            logger.info("Reset password code matched for user: {}", userFromDb.email());
                                            return userAuthPort.changeUserPassword(Mono.just(userFromDb.email()), userEmailCodeAndPassword.password())
                                                    .then(Mono.defer(() ->
                                                            userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(userFromDb.id()))
                                                                    .doOnSuccess(ignored -> logger.info("Reset password code deleted for user: {}", userFromDb.email()))
                                                                    .thenReturn(Result.success(new Status(true)))
                                                    ))
                                                    .doOnError(ex -> logger.error("Error while changing password for user: {}", userFromDb.email(), ex));
                                        } else {
                                            logger.warn("Wrong reset password code for user: {}", userFromDb.email());
                                            return Mono.just(Result.<Status>error(ErrorMessage.BAD_CHANGE_PASSWORD_CODE.getMessage()));
                                        }
                                    })
                                    .doOnError(ex -> logger.error("Error fetching reset password code for user: {}", userFromDb.email(), ex));
                        })
                        .onErrorResume(RuntimeException.class, ex -> {
                            logger.error("Runtime exception during password change process", ex);
                            return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                                    logger.warn("No user found with email: {}", userEmailCodeAndPassword.email());
                                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                                })
                        )
        ).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
    }


    private UserMyChat mapToUser(UserRegisterDataDTO userRegisterDataDTO) {
        return new UserMyChat(null, userRegisterDataDTO.name(), userRegisterDataDTO.surname(), userRegisterDataDTO.email());
    }

    private Mono<CodeVerification> generateAndSaveVerificationCode(UserMyChat savedUserMyChat) {
        String code = generateRandomCodePort.generateCode();
        return userRepositoryPort.saveVerificationCode(new CodeVerification(null, savedUserMyChat.id(), code))
                .doOnSuccess(savedCode -> {
                    sendEmail.sendVerificationCode(savedUserMyChat.email(), code);
                    logger.info("Verification code sent to user email: {}", savedUserMyChat.email());
                }).onErrorResume(ex -> {
                    logger.error("Error sending verification code for user ID: {}", savedUserMyChat.id(), ex);
                    return Mono.error(new SendVerificationCodeException(ex));
                });
    }


    @Override
    public Mono<Void> registerUser(UserRegisterDataDTO userRegisterDataDTO) {
        return userAuthPort.register(userRegisterDataDTO)
                .then(Mono.defer(() -> {
                    UserMyChat newUserMyChat = mapToUser(userRegisterDataDTO);
                    return userRepositoryPort.saveUser(newUserMyChat)
                            .doOnSuccess(user -> logger.info("User saved in repository with ID: {}", user.id()))
                            .flatMap(savedUser -> generateAndSaveVerificationCode(savedUser)
                                    .onErrorResume(ex -> {
                                        logger.error("Error saving verification code for user ID: {}", savedUser.id(), ex);
                                        return Mono.error(new SaveDataInRepositoryException("Error saving verification code", ex));
                                    })
                            )
                            .onErrorResume(ex -> {
                                logger.error("Error saving user in repository", ex);
                                return Mono.error(new SaveDataInRepositoryException("Error saving user in repository", ex));
                            });

                }))
                .then()
                .doOnSuccess(user -> logger.info("User registration successful for email: {}", userRegisterDataDTO.email()));
    }

    @Override
    public Mono<UserAccessData> login(LoginData loginData) {
        return userAuthPort.isEmailAlreadyActivatedUserAccount(loginData.email())
                .flatMap(isActive -> {
                    if (!isActive) {
                        logger.warn("User account is inactive: {}", loginData.email());
                        return Mono.error(new AuthServiceException("User account is inactive"));
                    }
                    return userAuthPort.authorizeUser(loginData)
                            .doOnError(ex -> logger.error("Error during user authorization", ex))
                            .onErrorResume(ex -> Mono.error(new AuthServiceException("Error during user authorization", ex)))
                            .doOnSuccess(userAccessData -> logger.info("User authorization successful for user: {}", loginData.email()));
                });
    }


    @Override
    public Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono) {
        return null;
//        return emailDataMono
//                .flatMap(emailData -> userRepositoryPort.findUserWithEmail(emailData.email())
//                        .flatMap(user -> {
//                            logger.info("User found with email: {}", user.email());
//                            return userAuthPort.isActivatedUserAccount(Mono.just(user.email()))
//                                    .flatMap(isActivated -> {
//                                        if (isActivated) {
//                                            logger.info("User account is activated for email: {}", user.email());
//                                            return userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(user.id()))
//                                                    .then(Mono.defer(() -> {
//                                                        String generatedCode = generateRandomCodePort.generateCode();
//                                                        logger.info("Generated reset password code for user: {}", user.email());
//                                                        sendEmail.sendResetPasswordCode(user.email(), generatedCode);
//                                                        logger.info("Reset password code sent to user email: {}", user.email());
//                                                        return userRepositoryPort.insertResetPasswordCode(new ResetPasswordCode(null, user.id(), generatedCode))
//                                                                .thenReturn(Result.success(new Status(true)));
//                                                    }))
//                                                    .onErrorResume(ex -> {
//                                                        logger.error("Error handling reset password code for user: {}", user.email(), ex);
//                                                        return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                                                    });
//                                        } else {
//                                            logger.warn("User account is not activated for email: {}", user.email());
//                                            return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                                        }
//                                    })
//                                    .switchIfEmpty(Mono.defer(() -> {
//                                        logger.warn("User account activation status not found for email: {}", user.email());
//                                        return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                                    }));
//                        })
//                )
//                .switchIfEmpty(Mono.defer(() -> {
//                    logger.warn("No user found with email");
//                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                }))
//                .onErrorResume(ex -> {
//                    logger.error("Unexpected error during reset password code sending process", ex);
//                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                });
    }

    @Override
    public Mono<Result<Status>> checkIsUserWithThisEmailExist(Mono<UserEmailData> userEmailDataMono) {
        return null;
//        return userEmailDataMono
//                .flatMap(userEmailData ->
//                        userRepositoryPort.findUserWithEmail(userEmailData.email())
//                                .flatMap(userFromDb -> {
//                                    logger.info("User found with email: {}", userFromDb.email());
//                                    return userAuthPort.isActivatedUserAccount(Mono.just(userFromDb.email()))
//                                            .flatMap(isActivated -> {
//                                                if (isActivated) {
//                                                    logger.info("User account is activated: {}", userFromDb.email());
//                                                    return Mono.just(Result.success(new Status(true)));
//                                                } else {
//                                                    logger.warn("User account is not activated: {}", userFromDb.email());
//                                                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                                                }
//                                            });
//                                })
//                                .switchIfEmpty(Mono.defer(() -> {
//                                    logger.info("User not found for email: {}", userEmailData.email());
//                                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                                }))
//                )
//                .onErrorResume(ex -> {
//                    logger.error("Error occurred while processing user with email: {}", ex.getMessage(), ex);
//                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                });
    }

    @Override
    public Mono<Result<UserData>> getUserAboutId(Mono<IdUserData> idUserDataMono) {
        return idUserDataMono
                .flatMap(idUserData ->
                        userRepositoryPort.findUserById(idUserData.idUser())
                                .flatMap(userFromDb -> {
                                    logger.info("User found with ID: {}", userFromDb.id());
                                    UserData userData = new UserData(userFromDb.id(), userFromDb.name(), userFromDb.surname(), userFromDb.email());
                                    return Mono.just(Result.success(userData));
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    logger.warn("User not found with ID: {}", idUserData.idUser());
                                    return Mono.just(Result.<UserData>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                                }))
                                .onErrorResume(ex -> {
                                    logger.error("Error retrieving user by ID: {}", idUserData.idUser(), ex);
                                    return Mono.just(Result.<UserData>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                                })
                );
    }

    @Override
    public Flux<UserData> getAllUsers() {
        return userRepositoryPort.findAllUsers().map(user -> new UserData(user.id(), user.name(), user.surname(), user.email())).doOnError(ex -> logger.error("Error occurred while fetching users", ex));
    }

    @Override
    public Flux<UserData> getUserMatchingNameAndSurname(Mono<String> patternNameMono) {
        return patternNameMono.flatMapMany(patternName -> {
                    String[] splitPattern = patternName.split(" ");
                    if (splitPattern.length > 1) {
                        String firstName = splitPattern[0].trim();
                        String lastName = splitPattern[1].trim();
                        logger.info("Searching for users with name '{}' and surname '{}'", firstName, lastName);
                        return userRepositoryPort.findUserMatchingNameAndSurname(firstName, lastName)
                                .map(user -> new UserData(user.id(), user.name(), user.surname(), user.email()))
                                .doOnError(ex -> logger.error("Error occurred while searching for users with name '{}' and surname '{}'", firstName, lastName, ex));
                    } else {
                        String trimmedPattern = patternName.trim();
                        logger.info("Searching for users with name or surname matching '{}'", trimmedPattern);
                        return userRepositoryPort.findUserMatchingNameOrSurname(trimmedPattern, trimmedPattern)
                                .map(user -> new UserData(user.id(), user.name(), user.surname(), user.email()))
                                .doOnError(ex -> logger.error("Error occurred while searching for users with name or surname matching '{}'", trimmedPattern, ex));
                    }
                })
                .onErrorResume(ex -> {
                    logger.error("Error during user search with pattern: {}", ex.getMessage(), ex);
                    return Flux.empty();
                });
    }

    @Override
    public Mono<Result<UserData>> getUserAboutEmail(Mono<UserEmailData> userEmailDataMono) {
        return userEmailDataMono
                .flatMap(userEmailData -> {
                    String email = userEmailData.email().trim();
                    logger.info("Fetching user with email: {}", email);
                    return userRepositoryPort.findUserWithEmail(email)
                            .flatMap(userFromDb -> {
                                logger.info("User found with email: {}", email);
                                UserData userData = new UserData(userFromDb.id(), userFromDb.name(), userFromDb.surname(), userFromDb.email());
                                return Mono.just(Result.success(userData));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                logger.info("No user found with email: {}", email);
                                return Mono.just(Result.<UserData>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                            }))
                            .doOnError(ex -> logger.error("Error occurred while fetching user with email: {}", email, ex));
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error occurred", ex);
                    return Mono.just(Result.<UserData>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                });
    }


    @Override
    public Mono<Result<Status>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono) {

        return emailAndCodeMono
                .flatMap(emailAndCode ->
                        userRepositoryPort.findUserWithEmail(emailAndCode.email())
                                .flatMap(userFromDb ->
                                        userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))
                                                .flatMap(codeFromDb -> {
                                                    if (codeFromDb.code().equals(emailAndCode.code())) {
                                                        logger.info("Reset password code is correct for email: {}", emailAndCode.email());
                                                        return Mono.just(Result.<Status>success(new Status(true)));
                                                    } else {
                                                        logger.warn("Incorrect reset password code for email: {}", emailAndCode.email());
                                                        return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                                                    }
                                                })
                                                .onErrorResume(ex -> {
                                                    logger.error("Error occurred while checking reset password code for user: ", ex);
                                                    return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                                                })
                                )
                )
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info("No user found");
                    return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                }))
                .onErrorResume(ex -> {
                    logger.error("Unexpected error occurred during code check", ex);
                    return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                });

    }

    @Override
    public Mono<Void> activateUserAccount(ActiveAccountCodeData codeVerificationMono) {
        return userRepositoryPort.findUserWithEmail(codeVerificationMono.email())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info("User to active not found with email: {}", codeVerificationMono.email());
                    return Mono.error(new ActivationCodeNotFoundException("User not found"));
                }))
                .flatMap(userActiveAccountData -> {
                    logger.info("User to active found with email: {}", userActiveAccountData.email());
                    return userRepositoryPort.findActiveUserAccountCodeForUserWithId(userActiveAccountData.id())
                            .switchIfEmpty(Mono.defer(() -> {
                                logger.info("Activation code not found for user ID: {}", userActiveAccountData.id());
                                return Mono.error(new ActivationCodeNotFoundException("Activation code not found"));
                            }))
                            .flatMap(codeVerificationSavedInDb -> {
                                logger.info("Verification code found for user ID: {}", userActiveAccountData.id());
                                if (codeVerificationSavedInDb.code().equals(codeVerificationMono.code())) {
                                    logger.info("Verification code matches for user ID: {}", userActiveAccountData.id());
                                    return userAuthPort.activateUserAccount(Mono.just(userActiveAccountData.email()))
                                            .then(Mono.defer(() -> userRepositoryPort.deleteUserActiveAccountCode(codeVerificationSavedInDb)))
                                            .doOnError(ex -> logger.error("Failed to activate user account for email: {}", userActiveAccountData.email(), ex));
                                } else {
                                    logger.warn("Bad activation code for user: {}", userActiveAccountData.email());
                                    return Mono.error(new BadActiveAccountCodeException("Bad code to activate user account"));
                                }
                            });
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    logger.error("Error during user account activation", ex);
                    return Mono.error(new UnexpectedInternalException("Error during user account activation", ex));
                });
    }

    @Override
    public Mono<Result<Status>> resendActiveUserAccountCode(Mono<UserEmailData> loginUserMono) {
        return null;
//        return loginUserMono.flatMap(loginUserData -> {
//                    logger.info("Received request to resend activation code for email: {}", loginUserData.email());
//                    return userRepositoryPort.findUserWithEmail(loginUserData.email())
//                            .switchIfEmpty(Mono.error(new NoSuchElementException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())))
//                            .flatMap(user -> {
//                                logger.info("User found with email: {}", user.email());
//                                return userAuthPort.isActivatedUserAccount(Mono.just(user.email()))
//                                        .flatMap(isActivated -> {
//                                            if (isActivated) {
//                                                logger.warn("User account is already active for email: {}", user.email());
//                                                return Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                                            } else {
//                                                logger.info("User account is not active for email: {}", user.email());
//                                                return Mono.just(user);
//                                            }
//                                        });
//                            })
//                            .flatMap(userMyChat -> {
//                                logger.info("Deleting existing activation code for user ID: {}", userMyChat.id());
//                                return userRepositoryPort.deleteUserActiveAccountCode(userMyChat.id())
//                                        .thenReturn(userMyChat);
//                            })
//                            .flatMap(userFromDb -> {
//                                String generatedCode = generateRandomCodePort.generateCode();
//                                logger.info("Generated new activation code for user ID: {}", userFromDb.id());
//
//                                return userRepositoryPort.saveVerificationCode(new CodeVerification(null, userFromDb.id(), generatedCode)).doOnNext(
//                                                x -> {
//                                                    logger.info("Sending verification code to email: {}", userFromDb.email());
//                                                    sendEmail.sendVerificationCode(userFromDb.email(), generatedCode);
//                                                }
//                                        )
//                                        .thenReturn(Result.success(new Status(true)));
//                            })
//                            .onErrorResume(NoSuchElementException.class, ex -> {
//                                logger.error("User not found: {}", ex.getMessage());
//                                return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
//                            });
//                })
//                .onErrorResume(ex -> {
//                    logger.error("Error during resendActiveUserAccountCode process: {}", ex.getMessage(), ex);
//                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage() + " " + ex.getMessage()));
//                });
    }
}
