package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.entity.UserRegisterData;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCode;
import com.example.usersservices_mychatserver.port.out.logic.HashPassword;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithVerificationCodePort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepositoryPort postgreUserRepository;
    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;
    private final HashPassword passwordHashService;

    private final GenerateRandomCode generateCode;

    private final SendEmailWithVerificationCodePort sendEmail;


    public RegisterUserService(UserRepositoryPort postgreUserRepository, CodeVerificationRepositoryPort postgreCodeVerificationRepository, HashPassword passwordHashService, GenerateRandomCode generateCode, SendEmailWithVerificationCodePort sendEmail) {
        this.postgreUserRepository = postgreUserRepository;
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
        this.passwordHashService = passwordHashService;
        this.generateCode = generateCode;
        this.sendEmail = sendEmail;
    }

    @Override
    public Mono<Result<UserMyChat>> registerUser(Mono<UserRegisterData> userRegisterDataMono) {
       return   userRegisterDataMono.flatMap(userRegisterData -> postgreUserRepository.findUserWithEmail(userRegisterData.email())
               .flatMap(userWithSameEmailInDatabase -> Mono.just(Result.<UserMyChat>error("User with this email already exists")))
               .switchIfEmpty(
                       Mono.just(Result.success(userRegisterData))
                               .map(newRegisteredUser -> new UserMyChat(
                                       null,
                                       newRegisteredUser.getValue().name(),
                                       newRegisteredUser.getValue().surname(),
                                       newRegisteredUser.getValue().email(),
                                       passwordHashService.cryptPassword(newRegisteredUser.getValue().password()),
                                       1,
                                       false
                               ))
                               .flatMap(userPreparedToSaveInDb -> postgreUserRepository.saveUser(userPreparedToSaveInDb)
                                       .map(newlyCreatedUser -> {
                                           String registerCode = generateCode.generateCode();
                                           CodeVerification codeVerification = new CodeVerification(null, newlyCreatedUser.id(), registerCode);
                                           postgreCodeVerificationRepository.saveVerificationCode(codeVerification)
                                                   .subscribeOn(Schedulers.boundedElastic())
                                                   .subscribe();
                                           sendEmail.sendVerificationCode(newlyCreatedUser, registerCode);
                                           return Result.success(newlyCreatedUser);
                                       }))
               ));


    }


}
