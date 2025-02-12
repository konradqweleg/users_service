package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.entity.response.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPort {
    Mono<Void> activateUserAccount(ActiveAccountCodeData codeVerificationMono);

    Mono<Void> resendActiveUserAccountCode(UserEmailDataDTO user);

    Mono<Void> sendResetPasswordCode(UserEmailDataDTO emailDataMono);

    Mono<IsCorrectResetPasswordCode> checkIsCorrectResetPasswordCode(UserEmailAndCodeDTO emailAndCode);

    Mono<Result<Status>> changeUserPassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono);

    Mono<Void> registerUser(UserRegisterDataDTO user);

    Mono<Result<Status>> checkIsUserWithThisEmailExist(Mono<UserEmailDataDTO> user);

    Mono<Result<UserData>>  getUserAboutId(Mono<IdUserData> idUserDataMono);

    Flux<UserData> getAllUsers();

    Flux<UserData> getUserMatchingNameAndSurname(String patternNameMono);
    Mono<UserData> getUserAboutEmail(UserEmailDataDTO userEmailDataMono);

    Mono<UserAccessData> login(LoginDataDTO userAuthorizeData);

}
