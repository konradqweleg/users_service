package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.entity.UserRegisterData;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<Result<UserMyChat>> registerUser(Mono<UserRegisterData> user);
}
