package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPort {
    Mono<Void> activateUserAccount(ActiveAccountCodeData codeVerificationMono);

    Mono<Void> resendActiveUserAccountCode(UserEmailData user);

    Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono);

    Mono<Result<Status>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono);

    Mono<Result<Status>> changeUserPassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono);

    Mono<Void> registerUser(UserRegisterDataDTO user);

    Mono<Result<Status>> checkIsUserWithThisEmailExist(Mono<UserEmailData> user);

    Mono<Result<UserData>>  getUserAboutId(Mono<IdUserData> idUserDataMono);

    Flux<UserData> getAllUsers();

    Flux<UserData> getUserMatchingNameAndSurname(Mono<String> patternNameMono);
    Mono<Result<UserData>> getUserAboutEmail(Mono<UserEmailData> userEmailDataMono);

    Mono<UserAccessData> login(LoginDataDTO userAuthorizeData);

}
