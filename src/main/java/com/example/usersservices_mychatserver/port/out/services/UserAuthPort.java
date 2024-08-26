package com.example.usersservices_mychatserver.port.out.services;

import com.example.usersservices_mychatserver.entity.request.UserAuthorizeData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import reactor.core.publisher.Mono;

public interface UserAuthPort {
    Mono<UserAccessData> authorizeUser(Mono<UserAuthorizeData> userAuthorizeData);

    Mono<Status>  registerNewUser(Mono<UserRegisterData> user);

    Mono<Status> activateUserAccount(Mono<String> email);

    Mono<Boolean> isActivatedUserAccount(Mono<String> email);

}
