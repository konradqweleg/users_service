package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.IsCorrectCredentials;
import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import reactor.core.publisher.Mono;

public interface UserPort {
    Mono<Result<Status>> activateUserAccount(Mono<ActiveAccountCodeData> codeVerificationMono);
    Mono<Result<Status>> resendActiveUserAccountCode(Mono<UserLoginData> user);
    Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono);

    Mono<Result<IsCorrectResetPasswordCode>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono);

    Mono<Result<Status>> changeUserPassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono);

    Mono<Result<IsCorrectCredentials>> isCorrectLoginCredentials(Mono<EmailAndPasswordData> user);

    Mono<Result<Status>> registerUser(Mono<UserRegisterData> user);
}
