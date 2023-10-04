package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.in.ActivateUserAccountUseCase;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ActiveUserAccountUseCaseService implements ActivateUserAccountUseCase {
    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;

    public ActiveUserAccountUseCaseService(CodeVerificationRepositoryPort postgreCodeVerificationRepository) {
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
    }

    @Override
    public Mono<Boolean> activateUserAccount(Mono<CodeVerification> codeVerificationMono) {
        return codeVerificationMono.subscribeOn(Schedulers.boundedElastic()).flatMap(
                postgreCodeVerificationRepository::saveActiveUserAccount
        );

    }
}
