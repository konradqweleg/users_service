package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPort {
    Mono<Result<Status>> activateUserAccount(Mono<ActiveAccountCodeData> codeVerificationMono);

    Mono<Result<Status>> resendActiveUserAccountCode(Mono<UserEmailData> user);

    Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono);

    Mono<Result<Status>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono);

    Mono<Result<Status>> changeUserPassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono);

    Mono<Result<IsCorrectCredentials>> isCorrectLoginCredentials(Mono<EmailAndPasswordData> user);

    Mono<Result<Status>> registerUser(Mono<UserRegisterData> user);

    Mono<Result<Status>> checkIsUserWithThisEmailExist(Mono<UserEmailData> user);

    Mono<Result<UserData>>  getUserAboutId(Mono<IdUserData> idUserDataMono);

    Flux<UserData> getAllUsers();

}
