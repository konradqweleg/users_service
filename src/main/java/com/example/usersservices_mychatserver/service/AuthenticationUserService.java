package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.request.EmailAndPasswordData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.entity.response.IsCorrectCredentials;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;

import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthenticationUserService implements com.example.usersservices_mychatserver.port.in.AuthenticationUserPort {
    private final  UserRepositoryPort userRepositoryPort;
    private final  ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort;

    private final  HashPasswordPort hashPasswordPort;

    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;

    private final GenerateRandomCodePort generateCode;

    private final SendEmailToUserPort sendEmail;

    private static final Integer ROLE_USER = 1;
    private static final Boolean DEFAULT_ACTIVE_IS_NOT_ACTIVE = false;


    private static final Logger log = LogManager.getLogger(AuthenticationUserService.class);

    AuthenticationUserService(UserRepositoryPort userRepositoryPort, ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort, HashPasswordPort hashPasswordPort, CodeVerificationRepositoryPort postgreCodeVerificationRepository, GenerateRandomCodePort generateCode, SendEmailToUserPort sendEmail) {
        this.userRepositoryPort = userRepositoryPort;
        this.hashPasswordPort = hashPasswordPort;
        this.resetPasswordCodeRepositoryPort = resetPasswordCodeRepositoryPort;
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
        this.generateCode = generateCode;
        this.sendEmail = sendEmail;
    }

    @Override
    public Mono<Result<Status>> changePassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono) {
        return userEmailAndCodeAndPasswordMono.flatMap(userEmailCodeAndPassword -> userRepositoryPort.findUserWithEmail(userEmailCodeAndPassword.email()).
                flatMap(userFromDb -> resetPasswordCodeRepositoryPort.findResetPasswordCodeForUser(new IdUserData(userFromDb.id())).flatMap(code -> {
                    if (code.code().equals(userEmailCodeAndPassword.code())) {
                        String newPassword = hashPasswordPort.cryptPassword(userEmailCodeAndPassword.password());
                        return userRepositoryPort.changePassword(userFromDb.id(), newPassword).
                                then(Mono.defer(() -> resetPasswordCodeRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(userFromDb.id())).
                                        thenReturn(Result.success(new Status(true)))));
                    } else {
                        return Mono.just(Result.<Status>error(ErrorMessage.BAD_CHANGE_PASSWORD_CODE.getMessage()));
                    }
                }).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_OR_RESET_PASSWORD_CODE_NOT_FOUND.getMessage())))).
                onErrorResume(RuntimeException.class, ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())))).
                switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_OR_RESET_PASSWORD_CODE_NOT_FOUND.getMessage())));
    }

    public Mono<Result<IsCorrectCredentials>> isCorrectCredentials(Mono<EmailAndPasswordData> userLoginDataMono) {
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
                .flatMap(userWithSameEmailInDatabase -> Mono.just(Result.<Status>error(ErrorMessage.USER_ALREADY_EXIST.getMessage())))
                .switchIfEmpty(Mono.defer(() -> {
                    try {
                        return registerNewUser(userRegisterData);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                    }
                }))

        ).onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
    }

    private Mono<Result<Status>> registerNewUser(UserRegisterData userRegisterData) {
        try {
            UserMyChat newUser = prepareUserForRegistration(userRegisterData);
            return userRepositoryPort.saveUser(newUser)
                    .flatMap(newlyCreatedUser -> {
                        String registerCode = generateCode.generateCode();
                        CodeVerification codeVerification = new CodeVerification(null, newlyCreatedUser.id(), registerCode);
                        return postgreCodeVerificationRepository.saveVerificationCode(codeVerification)
                                .flatMap(sendSavedVerificationCode -> {
                                    sendEmail.sendVerificationCode(newlyCreatedUser, registerCode);
                                    return Mono.just(Result.success(newlyCreatedUser));
                                })
                                .thenReturn(Result.success(new Status(true)));
                    })
                    .onErrorResume(DataAccessException.class, ex ->{
                       log.error(ex.getMessage());
                       return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
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

}
