package com.example.usersservices_mychatserver.port.out.persistence;

import com.example.usersservices_mychatserver.model.CodeVerification;
import reactor.core.publisher.Mono;

public interface CodeVerificationRepositoryPort {
    Mono<CodeVerification> findUserActiveAccountCodeById(Long idUser);

    Mono<CodeVerification> saveVerificationCode(CodeVerification code);

    Mono<Void> deleteUserActivationCode(CodeVerification codeVerification1);
}
