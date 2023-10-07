package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.ActiveUserAccountDataResponse;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.entity.Result;
import reactor.core.publisher.Mono;

public interface ActivateUserAccountUseCase {
    Mono<Result<ActiveUserAccountDataResponse>> activateUserAccount(Mono<CodeVerification> codeVerificationMono);
}
