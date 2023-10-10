package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.LoginAndPasswordData;
import com.example.usersservices_mychatserver.entity.response.IsCorrectCredentials;
import com.example.usersservices_mychatserver.entity.response.Result;
import reactor.core.publisher.Mono;

public interface LogInUseCase {
    Mono<Result<IsCorrectCredentials>> logIn(Mono<LoginAndPasswordData> user);
}
