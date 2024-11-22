package com.example.usersservices_mychatserver.port.out.services;

import com.example.usersservices_mychatserver.entity.request.LoginData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import reactor.core.publisher.Mono;

public interface UserAuthPort {
    Mono<UserAccessData> authorizeUser(LoginData userAuthorizeData);
    Mono<Void> register(UserRegisterDataDTO registerData);

    Mono<Status> activateUserAccount(Mono<String> email);

    Mono<Boolean> isActivatedUserAccount(String email);

    Mono<Status> changeUserPassword(Mono<String> email,String newPassword);

}
