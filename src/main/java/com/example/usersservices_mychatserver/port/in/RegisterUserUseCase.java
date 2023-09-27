package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.model.UserMyChat;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<UserMyChat> registerUser(Mono<UserMyChat> user);
}
