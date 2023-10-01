package com.example.usersservices_mychatserver.port.in;

import reactor.core.publisher.Mono;

public interface ActivateUserAccount {
    Mono<Boolean> activateUserAccount(Integer idUser, String code);
}
