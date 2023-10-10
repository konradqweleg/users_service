package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.entity.Status;
import reactor.core.publisher.Mono;

public interface ChangePasswordUseCase {
    Mono<Result<Status>> changePassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono);
}
