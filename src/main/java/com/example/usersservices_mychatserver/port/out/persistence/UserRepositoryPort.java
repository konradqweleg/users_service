package com.example.usersservices_mychatserver.port.out.persistence;
import com.example.usersservices_mychatserver.model.UserMyChat;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<UserMyChat> saveUser(UserMyChat user);
    Mono<UserMyChat> findUserWithEmail(String email);

    Mono<UserMyChat> activeUserAccount(Long idUser);


}
