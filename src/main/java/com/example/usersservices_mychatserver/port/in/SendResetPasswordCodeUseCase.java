package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import reactor.core.publisher.Mono;

public interface SendResetPasswordCodeUseCase {
     Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono);
}
