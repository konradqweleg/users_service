package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.model.CodeVerification;
import reactor.core.publisher.Mono;

public interface ActivateUserAccountUseCase {
    Mono<Boolean> activateUserAccount(Mono<CodeVerification> codeVerificationMono);
}
