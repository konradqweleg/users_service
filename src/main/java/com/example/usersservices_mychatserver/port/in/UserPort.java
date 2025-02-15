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

    Mono<Void> changeUserPassword(ChangePasswordData changePasswordData);

    Mono<Void> registerUser(UserRegisterDataDTO user);

    Mono<Boolean> checkIsUserWithThisEmailExist(UserEmailDataDTO user);

    Mono<UserData> getUserAboutId(IdUserData idUser);

    Flux<UserData> getAllUsers();

    Flux<UserData> getUserMatchingNameAndSurname(String patternNameMono);

    Mono<UserData> getUserAboutEmail(UserEmailDataDTO userEmailDataMono);

    Mono<UserAccessData> login(LoginDataDTO userAuthorizeData);

}
