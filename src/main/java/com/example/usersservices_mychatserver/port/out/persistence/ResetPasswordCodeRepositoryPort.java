package com.example.usersservices_mychatserver.port.out.persistence;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import reactor.core.publisher.Mono;

public interface ResetPasswordCodeRepositoryPort {
    Mono<Void> insertResetPasswordCode(ResetPasswordCode resetPasswordCode);
    Mono<Void> deleteResetPasswordCodeForUser(IdUserData idUser);

    Mono<ResetPasswordCode> findResetPasswordCodeForUser(IdUserData idUser);
}
