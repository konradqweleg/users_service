package com.example.usersservices_mychatserver.repository;

import com.example.usersservices_mychatserver.model.CodeVerification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CodeVerificationRepository extends ReactiveCrudRepository<CodeVerification, Long> {
    Mono<CodeVerification> findByIdUser(Long id);

    Mono<Void> deleteByIdUser(Long idUser);
}
