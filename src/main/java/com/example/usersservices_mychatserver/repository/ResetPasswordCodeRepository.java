package com.example.usersservices_mychatserver.repository;

import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ResetPasswordCodeRepository extends ReactiveCrudRepository<ResetPasswordCode, Long> {

    Mono<Void> deleteByIdUser(Long idUser);
    Mono<ResetPasswordCode> findResetPasswordCodeByIdUser(Long idUser);
}
