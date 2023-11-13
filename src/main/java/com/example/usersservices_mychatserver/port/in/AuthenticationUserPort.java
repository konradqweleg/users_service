package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.IsCorrectCredentials;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import reactor.core.publisher.Mono;

public interface AuthenticationUserPort {
    Mono<Result<Status>> changePassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono);

    Mono<Result<IsCorrectCredentials>> isCorrectCredentials(Mono<LoginAndPasswordData> user);

    Mono<Result<Status>> registerUser(Mono<UserRegisterData> user);


}
