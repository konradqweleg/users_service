package com.example.usersservices_mychatserver.port.out.persistence;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<CodeVerification> findActiveUserAccountCodeForUserWithId(Long idUser);

    Mono<CodeVerification> saveVerificationCode(CodeVerification code);

    Mono<Void> deleteUserActiveAccountCode(Long idUser);

    Mono<Void> deleteUserActiveAccountCode(CodeVerification codeVerification1);

    Mono<Void> insertResetPasswordCode(ResetPasswordCode resetPasswordCode);
    Mono<Void> deleteResetPasswordCodeForUser(IdUserData idUser);

    Mono<ResetPasswordCode> findResetPasswordCodeForUserById(IdUserData idUser);

    Mono<UserMyChat> saveUser(UserMyChat user);
    Mono<UserMyChat> findUserWithEmail(String email);

    Mono<UserMyChat> findUserById(Long idUser);

    Flux<UserMyChat> findAllUsers();

    Flux<UserMyChat> findUserMatchingNameOrSurname(String patternName, String patternSurname);

    Flux<UserMyChat> findUserMatchingNameAndSurname(String patternName, String patternSurname);
}
