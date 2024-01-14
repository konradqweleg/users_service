package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.IsCorrectCredentials;
import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.function.Function;

@Service
public class UserService implements UserPort {
    private final UserRepositoryPort userRepositoryPort;
    private final SendEmailToUserPort sendEmail;
    private final GenerateRandomCodePort generateRandomCodePort;
    private final HashPasswordPort hashPasswordPort;


    private static final Integer ROLE_USER = 1;
    private static final Boolean DEFAULT_ACTIVE_IS_NOT_ACTIVE = false;
    public UserService(UserRepositoryPort userRepositoryPort, SendEmailToUserPort sendEmail, GenerateRandomCodePort generateRandomCodePort, HashPasswordPort hashPasswordPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.sendEmail = sendEmail;
        this.generateRandomCodePort = generateRandomCodePort;
        this.hashPasswordPort = hashPasswordPort;
    }

    @Override
    public Mono<Result<Status>> changeUserPassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono) {
        return userEmailAndCodeAndPasswordMono.flatMap(userEmailCodeAndPassword -> userRepositoryPort.findUserWithEmail(userEmailCodeAndPassword.email()).
                        flatMap(userFromDb -> userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id())).flatMap(code -> {
                            if (code.code().equals(userEmailCodeAndPassword.code())) {
                                String newPassword = hashPasswordPort.cryptPassword(userEmailCodeAndPassword.password());
                                return userRepositoryPort.changePassword(userFromDb.id(), newPassword).
                                        then(Mono.defer(() -> userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(userFromDb.id())).
                                                thenReturn(Result.success(new Status(true)))));
                            } else {
                                return Mono.just(Result.<Status>error(ErrorMessage.BAD_CHANGE_PASSWORD_CODE.getMessage()));
                            }
                        })).
                        onErrorResume(RuntimeException.class, ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())))).
                switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_OR_RESET_PASSWORD_CODE_NOT_FOUND.getMessage())));

    }

    public Mono<Result<IsCorrectCredentials>> isCorrectLoginCredentials(Mono<EmailAndPasswordData> userLoginDataMono) {
        return userLoginDataMono.flatMap(userLoginData -> userRepositoryPort.findUserWithEmail(userLoginData.email())
                        .flatMap(userFromDb -> {
                            if (hashPasswordPort.checkPassword(userLoginData.password(), userFromDb.password())) {
                                if(userFromDb.isActiveAccount()) {
                                    return Mono.just(Result.<IsCorrectCredentials>success(new IsCorrectCredentials(true)));
                                } else {
                                    return Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
                                }
                            } else {
                                return Mono.just(Result.<IsCorrectCredentials>success(new IsCorrectCredentials(false)));
                            }
                        })
                        .switchIfEmpty(Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.USER_NOT_FOUND.getMessage()))))
                .onErrorResume(ex -> Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
    }


    @Override
    public Mono<Result<Status>> registerUser(Mono<UserRegisterData> userRegisterDataMono) {
        return userRegisterDataMono.flatMap(userRegisterData -> userRepositoryPort.findUserWithEmail(userRegisterData.email())
                .flatMap(userWithSameEmailInDatabase -> {
                            if (userWithSameEmailInDatabase.isActiveAccount()) {
                                return Mono.just(Result.<Status>error(ErrorMessage.USER_ALREADY_EXIST.getMessage()));
                            } else {
                                return Mono.just(Result.<Status>error(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
                            }

                        }

                )
                .switchIfEmpty(Mono.defer(() -> {
                    try {
                        return registerNewUser(userRegisterData);
                    } catch (Exception e) {

                        return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                    }
                }))

        ).onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
    }


    @Override
    public Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono) {

        return emailDataMono.flatMap( emailData -> userRepositoryPort.findUserWithEmail(emailData.email())).flatMap(userFromDb->{
            if(userFromDb.isActiveAccount()){
                return Mono.just(userFromDb).flatMap(user -> userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(user.id())).
                        thenReturn(user)).
                        flatMap(user -> {
                            String generatedCode = generateRandomCodePort.generateCode();
                            sendEmail.sendResetPasswordCode(user.email(), generatedCode);
                            return userRepositoryPort.insertResetPasswordCode(new ResetPasswordCode(null, user.id(), generatedCode)).
                                    thenReturn(Result.success(new Status(true)));
                        }).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())));
            } else {
                return Mono.just(Result.<Status>error(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
            }
        }).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
                .onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

//        return emailDataMono.flatMap(emailData -> {
//
//            return userRepositoryPort.findUserWithEmail(emailData.email()).flatMap(user -> {
//                        if (user.isActiveAccount()) {
//                            return Mono.just(user);
//                        } else {
//                            return Mono.error(new RuntimeException(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
//                        }
//                    }).flatMap(user -> userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(user.id())).
//                            thenReturn(user)).
//                    flatMap(user -> {
//                        String generatedCode = generateRandomCodePort.generateCode();
//                        sendEmail.sendResetPasswordCode(user.email(), generatedCode);
//                        return userRepositoryPort.insertResetPasswordCode(new ResetPasswordCode(null, user.id(), generatedCode)).
//                                thenReturn(Result.success(new Status(true)));
//                    }).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())));
//        }).onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

    }
    @Override
    public Mono<Result<Status>> checkIsUserWithThisEmailExist(Mono<UserEmailData> user) {
        return user.flatMap(userEmailData -> userRepositoryPort.findUserWithEmail(userEmailData.email())
                .flatMap(userFromDb -> {
                    if (userFromDb.isActiveAccount()) {
                        return Mono.just(Result.success(new Status(true)));
                    } else {
                        return Mono.just(Result.<Status>error(ErrorMessage.ACCOUNT_NOT_ACTIVE.getMessage()));
                    }
                })
                .switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
                .onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()))));
    }

    private Mono<Result<Status>> registerNewUser(UserRegisterData userRegisterData) {
        try {
            UserMyChat newUser = prepareUserForRegistration(userRegisterData);
            return userRepositoryPort.saveUser(newUser)
                    .flatMap(newlyCreatedUser -> {
                        String registerCode = generateRandomCodePort.generateCode();
                        CodeVerification codeVerification = new CodeVerification(null, newlyCreatedUser.id(), registerCode);
                        return userRepositoryPort.saveVerificationCode(codeVerification)
                                .flatMap(sendSavedVerificationCode -> {
                                    sendEmail.sendVerificationCode(newlyCreatedUser, registerCode);
                                    return Mono.just(Result.success(newlyCreatedUser));
                                })
                                .thenReturn(Result.success(new Status(true)));
                    })
                    .onErrorResume(DataAccessException.class, ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
        } catch (Exception e) {

            return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
        }
    }

    private UserMyChat prepareUserForRegistration(UserRegisterData userRegisterData) {
        return new UserMyChat(
                null,
                userRegisterData.name(),
                userRegisterData.surname(),
                userRegisterData.email(),
                hashPasswordPort.cryptPassword(userRegisterData.password()),
                ROLE_USER,
                DEFAULT_ACTIVE_IS_NOT_ACTIVE);

    }



    @Override
    public Mono<Result<Status>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono) {

        return emailAndCodeMono.flatMap(emailAndCode ->
                userRepositoryPort.findUserWithEmail(emailAndCode.email())
                        .flatMap(userFromDb ->
                                userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))
                                        .flatMap(codeFromDb->{
                                            if(codeFromDb.code().equals(emailAndCode.code())){
                                                return Mono.just(Result.<Status>success(new Status(true)));
                                            } else {
                                                return Mono.just(Result.<Status>error(ErrorMessage.WRONG_RESET_PASSWORD_CODE.getMessage()));
                                            }
                                        })


                        )
        ).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.RESET_PASSWORD_CODE_NOT_FOUND.getMessage())))
        .onErrorResume(ex -> {
            System.out.println(ex.getMessage());
           return Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));});





    }


//        return emailAndCodeMono.flatMap(emailAndCode -> userRepositoryPort.findUserWithEmail(emailAndCode.email()).
//                        flatMap(userFromDb -> userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id())).flatMap(codeFromDb -> {
//                            if (codeFromDb.code().equals(emailAndCode.code())) {
//                                return Mono.just(Result.<IsCorrectResetPasswordCode>success(new IsCorrectResetPasswordCode(true)));
//                            } else {
//                                return Mono.just(Result.<IsCorrectResetPasswordCode>success(new IsCorrectResetPasswordCode(false)));
//                            }
//                        }).switchIfEmpty(Mono.just(Result.<IsCorrectResetPasswordCode>error(ErrorMessage.RESET_PASSWORD_CODE_NOT_FOUND.getMessage()))))
//                        .onErrorResume(NoSuchElementException.class, ex -> Mono.just(Result.<IsCorrectResetPasswordCode>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
//                )
//                .onErrorResume(RuntimeException.class,ex -> Mono.just(Result.<IsCorrectResetPasswordCode>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

    @Override
    public Mono<Result<Status>> activateUserAccount(Mono<ActiveAccountCodeData> codeVerificationMono) {

        Mono<ActiveAccountCodeData> cacheActiveAccountCodeDataMono = codeVerificationMono.cache();

        return cacheActiveAccountCodeDataMono.flatMap(codeActiveAccount -> userRepositoryPort.findUserWithEmail(codeActiveAccount.email()).flatMap(
                        userActiveAccountData -> userRepositoryPort.findActiveUserAccountCodeForUserWithId(userActiveAccountData.id()).flatMap(
                                codeVerificationSavedInDb -> cacheActiveAccountCodeDataMono.flatMap(userActiveAccountCodeFromRequest -> {
                                            if (codeVerificationSavedInDb.code().equals(userActiveAccountCodeFromRequest.code())) {
                                                return userRepositoryPort.activeUserAccount(codeVerificationSavedInDb.idUser()).
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

        return loginUserMono.flatMap(loginUserData -> {

                    Mono<UserMyChat> userData = userRepositoryPort.findUserWithEmail(loginUserData.email());
                    if (userData == null) {
                        return Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage()));
                    }

                    return userData.flatMap(user -> {
                                if (user.isActiveAccount()) {
                                    return Mono.error(new RuntimeException(ErrorMessage.USER_ALREADY_ACTIVE.getMessage()));
                                } else {
                                    return Mono.just(user);
                                }
                            }).flatMap(userMyChat -> userRepositoryPort.deleteUserActiveAccountCode(userMyChat.id())
                                    .thenReturn(userMyChat))
                            .flatMap(userFromDb -> {
                                String generatedCode = generateRandomCodePort.generateCode();
                                sendEmail.sendVerificationCode(userFromDb, generatedCode);
                                return userRepositoryPort.saveVerificationCode(new CodeVerification(null, userFromDb.id(), generatedCode))
                                        .thenReturn(Result.success(new Status(true)));
                            })
                            .switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())))
                            .onErrorResume(NoSuchElementException.class, ex -> Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())));
                })
                .onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage() + " " + ex.getMessage())));
    }
}
