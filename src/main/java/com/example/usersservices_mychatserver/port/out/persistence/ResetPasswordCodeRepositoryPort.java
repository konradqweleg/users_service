package com.example.usersservices_mychatserver.port.out.persistence;

import com.example.usersservices_mychatserver.entity.IdUserData;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import reactor.core.publisher.Mono;

public interface ResetPasswordCodeRepositoryPort {
    Mono<Void> insertResetPasswordCode(ResetPasswordCode resetPasswordCode);
    Mono<Void> deleteResetPasswordCodeForUser(IdUserData idUser);
}
