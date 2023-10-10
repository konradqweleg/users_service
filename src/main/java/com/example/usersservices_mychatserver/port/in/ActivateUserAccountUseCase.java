package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.entity.response.Result;
import reactor.core.publisher.Mono;

public interface ActivateUserAccountUseCase {
    Mono<Result<Status>> activateUserAccount(Mono<CodeVerification> codeVerificationMono);
}
