package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<Result<Status>> registerUser(Mono<UserRegisterData> user);
}
