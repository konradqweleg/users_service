package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.LoginAndPasswordData;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.entity.Status;
import com.example.usersservices_mychatserver.entity.UserEmailData;
import reactor.core.publisher.Mono;

public interface SendResetPasswordCodeUseCase {
     Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono);
}
