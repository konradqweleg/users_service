package com.example.usersservices_mychatserver.port.out.services;

import com.example.usersservices_mychatserver.entity.request.LoginDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import reactor.core.publisher.Mono;

public interface UserAuthPort {
    Mono<UserAccessData> authorizeUser(LoginDataDTO userAuthorizeData);

    Mono<Void> register(UserRegisterDataDTO registerData);

    Mono<Void> activateUserAccount(String email);

    Mono<Boolean> isEmailAlreadyActivatedUserAccount(String email);

    Mono<Void> changeUserPassword(String email, String newPassword);

    Mono<Boolean> isEmailAlreadyRegistered(String email);

    Mono<UserAccessData> refreshAccessToken(String refreshToken);

}
