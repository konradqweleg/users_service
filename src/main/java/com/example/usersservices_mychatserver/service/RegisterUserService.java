package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithVerificationCodePort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepositoryPort postgreUserRepository;
    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;
    private final HashPasswordPort passwordHashService;

    private final GenerateRandomCodePort generateCode;

    private final SendEmailWithVerificationCodePort sendEmail;
    private static final Integer ROLE_USER = 1;
    private static final Boolean DEFAULT_ACTIVE_IS_NOT_ACTIVE = false;




    public RegisterUserService(UserRepositoryPort postgreUserRepository, CodeVerificationRepositoryPort postgreCodeVerificationRepository, HashPasswordPort passwordHashService, GenerateRandomCodePort generateCode, SendEmailWithVerificationCodePort sendEmail) {
        this.postgreUserRepository = postgreUserRepository;
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
        this.passwordHashService = passwordHashService;
        this.generateCode = generateCode;
        this.sendEmail = sendEmail;
    }


    @Override
    public Mono<Result<Status>> registerUser(Mono<UserRegisterData> userRegisterDataMono) {
        return userRegisterDataMono.flatMap(userRegisterData -> postgreUserRepository.findUserWithEmail(userRegisterData.email())
                .flatMap(userWithSameEmailInDatabase -> Mono.just(Result.<Status>error(ErrorMessage.USER_ALREADY_EXISTS.getMessage())))
                .switchIfEmpty(Mono.defer(() -> {
                    try {
                        return registerNewUser(userRegisterData);
                    } catch (Exception e) {
                        return Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()));
                    }
                })));
    }

    private Mono<Result<Status>> registerNewUser(UserRegisterData userRegisterData) {
        try {
            UserMyChat newUser = prepareUserForRegistration(userRegisterData);
            return postgreUserRepository.saveUser(newUser)
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
                passwordHashService.cryptPassword(userRegisterData.password()),
                ROLE_USER,
                DEFAULT_ACTIVE_IS_NOT_ACTIVE
        );
    }




}
