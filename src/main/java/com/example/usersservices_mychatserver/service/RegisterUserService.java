package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCode;
import com.example.usersservices_mychatserver.port.out.logic.HashPassword;
import com.example.usersservices_mychatserver.port.out.persistence.RepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RegisterUserService implements RegisterUserUseCase {
    private final RepositoryPort postgreRepository;
    private final HashPassword passwordHashService;

    private final GenerateRandomCode generateCode;


    public RegisterUserService(RepositoryPort postgreRepository, HashPassword passwordHashService, GenerateRandomCode generateCode) {
        this.postgreRepository = postgreRepository;
        this.passwordHashService = passwordHashService;
        this.generateCode = generateCode;
    }

    @Override
    public Mono<UserMyChat> registerUser(Mono<UserMyChat> user) {
        return user.map(
                userWithoutHashPassword -> {
                    String userPasswordHashed = passwordHashService.cryptPassword(userWithoutHashPassword.password());
                    return userWithoutHashPassword.withNewPassword(userPasswordHashed);
                }
        ).flatMap(postgreRepository::saveUser).doOnNext(createdUser -> {
            String registerCode = generateCode.generateCode();
            CodeVerification codeVerification = new CodeVerification(null, createdUser.id(), registerCode);
            postgreRepository.saveVerificationCode(codeVerification).subscribeOn(Schedulers.boundedElastic()).subscribe();
        });
    }
}
