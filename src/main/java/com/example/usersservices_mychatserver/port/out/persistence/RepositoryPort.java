package com.example.usersservices_mychatserver.port.out.persistence;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import reactor.core.publisher.Mono;

public interface RepositoryPort {
    Mono<UserMyChat> saveUser(UserMyChat user);
    Mono<CodeVerification> saveVerificationCode(CodeVerification code);

    Mono<UserMyChat> findUserWithEmail(String email);

    Mono<Boolean> saveActiveUserAccount(Integer idUser, String code);

    Mono<CodeVerification> checkIsActiveAccountCodeSend(Integer idUser);


}
