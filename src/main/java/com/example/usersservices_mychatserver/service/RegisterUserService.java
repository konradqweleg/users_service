package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
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
    public Mono<UserMyChat> registerUser(Mono<UserMyChat> user) {
        return user.map(
                        userWithoutHashPassword -> {
                            String userPasswordHashed = passwordHashService.cryptPassword(userWithoutHashPassword.password());
                            return userWithoutHashPassword.withNewPassword(userPasswordHashed);
                        }
                ).flatMap(postgreUserRepository::saveUser)
                .doOnNext(createdUser -> {
                    String registerCode = generateCode.generateCode();
                    CodeVerification codeVerification = new CodeVerification(null,createdUser.id(), registerCode);
                    postgreCodeVerificationRepository.saveVerificationCode(codeVerification).subscribeOn(Schedulers.boundedElastic()).subscribe();
                    sendEmail.sendVerificationCode(createdUser, registerCode);
                });
    }


}
