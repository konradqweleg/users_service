package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeData;
import reactor.core.publisher.Mono;

public interface CheckIsCorrectResetPasswordCodeUseCase {
    Mono<Result<IsCorrectResetPasswordCode>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono);
}
