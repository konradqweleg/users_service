package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.LoginAndPasswordData;
import com.example.usersservices_mychatserver.entity.response.IsCorrectCredentials;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.port.in.LogInUseCase;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LogInService implements LogInUseCase {
    private final HashPasswordPort passwordHashService;
    private final UserRepositoryPort postgreUserRepository;

    public LogInService(HashPasswordPort passwordHashService, UserRepositoryPort postgreUserRepository) {
        this.passwordHashService = passwordHashService;
        this.postgreUserRepository = postgreUserRepository;
    }

    @Override
    public Mono<Result<IsCorrectCredentials>> logIn(Mono<LoginAndPasswordData> userLoginDataMono) {
        return userLoginDataMono.flatMap(userLoginData -> postgreUserRepository.findUserWithEmail(userLoginData.login())
                .flatMap(userFromDb -> {
                    if (passwordHashService.checkPassword(userLoginData.password(), userFromDb.password())) {
                        return Mono.just(Result.<IsCorrectCredentials>success(new IsCorrectCredentials(true)));
                    } else {
                        return Mono.just(Result.<IsCorrectCredentials>success(new IsCorrectCredentials(false)));
                    }
                })
                .switchIfEmpty(Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.USER_NOT_FOUND.getMessage()))))
                .onErrorResume(ex -> Mono.just(Result.<IsCorrectCredentials>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
    }
}
