package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.port.in.ActivateUserAccountUseCase;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ActiveUserAccountService implements ActivateUserAccountUseCase {
    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;
    private final UserRepositoryPort userRepository;


    public ActiveUserAccountService(CodeVerificationRepositoryPort postgreCodeVerificationRepository, UserRepositoryPort userRepository) {
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Result<Status>> activateUserAccount(Mono<CodeVerification> codeVerificationMono) {
        return codeVerificationMono.flatMap(
                codeVerificationProvidedByUser -> postgreCodeVerificationRepository.findUserActiveAccountCodeById(codeVerificationProvidedByUser.idUser()).flatMap(
                                codeVerificationSaved -> {
                                    if (codeVerificationSaved.code().equals(codeVerificationProvidedByUser.code())) {
                                        return userRepository.activeUserAccount(codeVerificationProvidedByUser.idUser()).
                                                then(Mono.defer(() -> postgreCodeVerificationRepository.deleteUserActivationCode(codeVerificationSaved).
                                                        thenReturn(Result.success(new Status(true)))));
                                    } else {
                                        return Mono.just(Result.<Status>error(ErrorMessage.BAD_CODE.getMessage()));
                                    }

                                }
                        ).switchIfEmpty(Mono.just(Result.error(ErrorMessage.CODE_NOT_FOUND_FOR_THIS_USER.getMessage()))).
                        onErrorResume(RuntimeException.class, ex -> Mono.just(Result.error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage()))));
    }
}
