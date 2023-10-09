package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.entity.Status;
import com.example.usersservices_mychatserver.entity.UserEmailAndCode;
import reactor.core.publisher.Mono;

public interface CheckIsCorrectResetPasswordCodeUseCase {
    Mono<Result<Status>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCode> emailAndCodeMono);
}
