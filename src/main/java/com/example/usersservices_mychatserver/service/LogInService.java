package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.LoginAndPasswordData;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.entity.Status;
import com.example.usersservices_mychatserver.port.in.LogInUseCase;
import com.example.usersservices_mychatserver.port.out.logic.HashPassword;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LogInService implements LogInUseCase {
    private final HashPassword passwordHashService;
    private final UserRepositoryPort postgreUserRepository;
    public LogInService(HashPassword passwordHashService, UserRepositoryPort postgreUserRepository) {
        this.passwordHashService = passwordHashService;
        this.postgreUserRepository = postgreUserRepository;
    }

    @Override
    public Mono<Result<Status>> logIn(Mono<LoginAndPasswordData> userLoginDataMono) {
        return userLoginDataMono.flatMap(userLoginData -> postgreUserRepository.findUserWithEmail(userLoginData.login())
                .flatMap(userFromDb -> {
                    if (passwordHashService.checkPassword(userLoginData.password(), userFromDb.password())) {
                        return Mono.just(Result.<Status>success(new Status(true)));
                    } else {
                        return Mono.just(Result.<Status>error("Bad credentials"));
                    }
                })
                .switchIfEmpty(Mono.just(Result.<Status>error("User not found"))));
    }
}
