package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.LoginAndPasswordData;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.entity.Status;
import reactor.core.publisher.Mono;

public interface LogInUseCase {
    Mono<Result<Status>> logIn(Mono<LoginAndPasswordData> user);
}
