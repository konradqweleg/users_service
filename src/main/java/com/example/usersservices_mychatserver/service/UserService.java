package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.*;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Collections;
import java.util.NoSuchElementException;


@Service
public class UserService implements UserPort {
    private final UserRepositoryPort userRepositoryPort;
    private final SendEmailToUserPort sendEmail;
    private final GenerateRandomCodePort generateRandomCodePort;

    private final UserAuthPort userAuthPort;

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private static final Integer ROLE_USER = 1;
    private static final Boolean DEFAULT_ACTIVE_IS_NOT_ACTIVE = false;

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
                                .flatMap(userFromDb ->
                                        userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))
                                                .flatMap(code -> {
                                                    if (code.code().equals(userEmailCodeAndPassword.code())) {
                                                        return userAuthPort.changeUserPassword(Mono.just(userFromDb.email()), userEmailCodeAndPassword.password())
                                                                .then(Mono.defer(() ->
                                                                        userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(userFromDb.id()))
                                                                                .thenReturn(Result.success(new Status(true)))
                                                                ));
                                                    } else {
                                                        return Mono.just(Result.<Status>error(ErrorMessage.BAD_CHANGE_PASSWORD_CODE.getMessage()));
                                                    }
                                                })
                                )
                                .onErrorResume(RuntimeException.class, ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())))
                )
                .switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_OR_RESET_PASSWORD_CODE_NOT_FOUND.getMessage())));

    }

//    public Mono<Result<IsCorrectCredentials>> isCorrectLoginCredentials(Mono<EmailAndPasswordData> userLoginDataMono) {
//        return userLoginDataMono.flatMap(userLoginData -> userRepositoryPort.findUserWithEmail(userLoginData.email())
//                        .flatMap(userFromDb -> {
//                            if (hashPasswordPort.checkPassword(userLoginData.password(), userFromDb.password())) {
//                                if (userFromDb.isActiveAccount()) {
//                                    return Mono.just(Result.<IsCorrectCredentials>success(new IsCorrectCredentials(true)));
//                                } else {
//                                    return Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
//                                }
//                            } else {
//                                return Mono.just(Result.<IsCorrectCredentials>success(new IsCorrectCredentials(false)));
//                            }
//                        })
//                        .switchIfEmpty(Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.USER_NOT_FOUND.getMessage()))))
//                .onErrorResume(ex -> Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
//    }

    @Override
    public Mono<Result<Status>> registerUser(Mono<UserRegisterData> userRegisterDataMono) {
        Mono<UserRegisterData> cachedUserRegisterDataMono = userRegisterDataMono.cache();
        return userAuthPort.registerNewUser(cachedUserRegisterDataMono).
                flatMap(result -> {
                    if (result.correctResponse()) {
                        return cachedUserRegisterDataMono.flatMap(x -> userRepositoryPort.saveUser(new UserMyChat(null, x.name(), x.surname(), x.email()))).
                                flatMap(userP -> {
                                    String code = generateRandomCodePort.generateCode();
                                    sendEmail.sendVerificationCode(userP.email(), code);
                                    Mono<CodeVerification> cverf = userRepositoryPort.saveVerificationCode(new CodeVerification(null, userP.id(), code));
                                    return cverf.flatMap(x -> Mono.just(userP));
                                }).
                                flatMap(user -> Mono.just(Result.success(new Status(true)))).
                                onErrorResume(ex -> Mono.just(Result.<Status>error(ex.getMessage())));
                    } else {
                        return Mono.error(new RuntimeException("User not registered"));
                    }
                }).onErrorResume(ex -> Mono.just(Result.<Status>error(ex.getMessage())));
    }

    @Override
    public Mono<Result<UserAccessData>> authorizeUser(Mono<UserAuthorizeData> userAuthorizeData) {
        return userAuthPort.authorizeUser(userAuthorizeData).map(Result::success).onErrorResume(ex -> Mono.just(Result.<UserAccessData>error(ex.getMessage())));
    }


    @Override
    public Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono) {

        return emailDataMono.flatMap(emailData -> userRepositoryPort.findUserWithEmail(emailData.email()))
                .flatMap(user -> userAuthPort.isActivatedUserAccount(Mono.just(user.email()))
                        .flatMap(isActivated -> {
                            if (isActivated) {
                                return userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(user.id()))
                                        .thenReturn(user)
                                        .flatMap(userFromDb -> {
                                            String generatedCode = generateRandomCodePort.generateCode();
                                            sendEmail.sendResetPasswordCode(userFromDb.email(), generatedCode);
                                            return userRepositoryPort.insertResetPasswordCode(new ResetPasswordCode(null, userFromDb.id(), generatedCode))
                                                    .thenReturn(Result.success(new Status(true)));
                                        })
                                        .switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())));
                            } else {
                                return Mono.just(Result.<Status>error(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
                            }
                        })
                )
                .switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
                .onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

    }

    @Override
    public Mono<Result<Status>> checkIsUserWithThisEmailExist(Mono<UserEmailData> user) {
        return user.flatMap(userEmailData -> userRepositoryPort.findUserWithEmail(userEmailData.email())
                .flatMap(userFromDb -> userAuthPort.isActivatedUserAccount(Mono.just(userFromDb.email()))
                        .flatMap(isActivated -> {
                            if (isActivated) {
                                return Mono.just(Result.success(new Status(true)));
                            } else {
                                logger.warn("User account is not activated: {}", userFromDb.email());
                                return Mono.just(Result.<Status>error(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
                            }
                        }))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info("User not found for email: {}", userEmailData.email());
                    return Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage()));
                })))
                .onErrorResume(ex -> {
                    logger.error("Error occurred while processing user: {}", ex.getMessage());
                    return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                });
    }

    @Override
    public Mono<Result<UserData>> getUserAboutId(Mono<IdUserData> idUserDataMono) {
        return idUserDataMono.flatMap(idUserData -> userRepositoryPort.findUserById(idUserData.idUser())
                .flatMap(userFromDb -> Mono.just(Result.success(new UserData(userFromDb.id(), userFromDb.name(), userFromDb.surname(), userFromDb.email()))).onErrorResume(ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()))))
                .switchIfEmpty(Mono.just(Result.<UserData>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
                .onErrorResume(ex -> Mono.just(Result.<UserData>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()))));
    }

    @Override
    public Flux<UserData> getAllUsers() {
        return userRepositoryPort.findAllUsers().map(user -> new UserData(user.id(), user.name(), user.surname(), user.email()));
    }

    @Override
    public Flux<UserData> getUserMatchingNameAndSurname(Mono<String> patternNameMono) {
        return patternNameMono.flatMapMany(patternName -> {
            String[] splitPattern = patternName.split(" ");
            if (splitPattern.length > 1) {
                return userRepositoryPort.findUserMatchingNameAndSurname(splitPattern[0].trim(), splitPattern[1].trim())
                        .map(user -> new UserData(user.id(), user.name(), user.surname(), user.email()));
            } else {
                return userRepositoryPort.findUserMatchingNameOrSurname(patternName.trim(), patternName.trim())
                        .map(user -> new UserData(user.id(), user.name(), user.surname(), user.email()));
            }
        });
    }

    @Override
    public Mono<Result<UserData>> getUserAboutEmail(Mono<UserEmailData> userEmailDataMono) {
        return userEmailDataMono.flatMap(userEmailData -> userRepositoryPort.findUserWithEmail(userEmailData.email())
                .flatMap(userFromDb -> Mono.just(Result.success(new UserData(userFromDb.id(), userFromDb.name(), userFromDb.surname(), userFromDb.email())))
                        .onErrorResume(ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())))
                )
                .switchIfEmpty(Mono.just(Result.<UserData>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
                .onErrorResume(ex -> Mono.just(Result.<UserData>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()))));
    }


    @Override
    public Mono<Result<Status>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono) {

        return emailAndCodeMono.flatMap(emailAndCode ->
                        userRepositoryPort.findUserWithEmail(emailAndCode.email())
                                .flatMap(userFromDb ->
                                        userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))
                                                .flatMap(codeFromDb -> {
                                                    if (codeFromDb.code().equals(emailAndCode.code())) {
                                                        return Mono.just(Result.<Status>success(new Status(true)));
                                                    } else {
                                                        return Mono.just(Result.<Status>error(ErrorMessage.WRONG_RESET_PASSWORD_CODE.getMessage()));
                                                    }
                                                })


                                )
                ).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.RESET_PASSWORD_CODE_NOT_FOUND.getMessage())))
                .onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

    }

    @Override
    public Mono<Result<Status>> activateUserAccount(Mono<ActiveAccountCodeData> codeVerificationMono) {

        Mono<ActiveAccountCodeData> cacheActiveAccountCodeDataMono = codeVerificationMono.cache();

        return cacheActiveAccountCodeDataMono.flatMap(codeActiveAccount -> userRepositoryPort.findUserWithEmail(codeActiveAccount.email()).flatMap(
                        userActiveAccountData -> userRepositoryPort.findActiveUserAccountCodeForUserWithId(userActiveAccountData.id()).flatMap(
                                codeVerificationSavedInDb -> cacheActiveAccountCodeDataMono.flatMap(userActiveAccountCodeFromRequest -> {
                                            if (codeVerificationSavedInDb.code().equals(userActiveAccountCodeFromRequest.code())) {

                                                return userAuthPort.activateUserAccount(Mono.just(userActiveAccountData.email())).
                                                        then(Mono.defer(() -> userRepositoryPort.deleteUserActiveAccountCode(codeVerificationSavedInDb).
                                                                thenReturn(Result.success(new Status(true)))));
                                            } else {
                                                return Mono.just(Result.<Status>error(ErrorMessage.BAD_CODE.getMessage()));
                                            }
                                        }
                                )
                        )).switchIfEmpty(Mono.just(Result.error(ErrorMessage.CODE_NOT_FOUND_FOR_THIS_USER.getMessage()))).
                onErrorResume(RuntimeException.class, ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()))));
    }

    @Override
    public Mono<Result<Status>> resendActiveUserAccountCode(Mono<UserEmailData> loginUserMono) {

        return loginUserMono.flatMap(loginUserData -> userRepositoryPort.findUserWithEmail(loginUserData.email())
                        .switchIfEmpty(Mono.error(new NoSuchElementException(ErrorMessage.USER_NOT_FOUND.getMessage())))
                        .flatMap(user -> userAuthPort.isActivatedUserAccount(Mono.just(user.email()))
                                .flatMap(isActivated -> {
                                    if (isActivated) {
                                        return Mono.error(new RuntimeException(ErrorMessage.USER_ALREADY_ACTIVE.getMessage()));
                                    } else {
                                        return Mono.just(user);
                                    }
                                })
                        )
                        .flatMap(userMyChat -> userRepositoryPort.deleteUserActiveAccountCode(userMyChat.id())
                                .thenReturn(userMyChat)
                        )
                        .flatMap(userFromDb -> {
                            String generatedCode = generateRandomCodePort.generateCode();

                            return userRepositoryPort.saveVerificationCode(new CodeVerification(null, userFromDb.id(), generatedCode)).doOnNext(
                                            x -> {
                                                sendEmail.sendVerificationCode(userFromDb.email(), generatedCode);
                                            }
                                    )
                                    .thenReturn(Result.success(new Status(true)));
                        })
                        .onErrorResume(NoSuchElementException.class, ex -> Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
                )
                .onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage() + " " + ex.getMessage())));
    }
}
