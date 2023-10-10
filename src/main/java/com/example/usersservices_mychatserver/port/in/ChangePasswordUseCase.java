package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import reactor.core.publisher.Mono;

public interface ChangePasswordUseCase {
    Mono<Result<Status>> changePassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono);
}
