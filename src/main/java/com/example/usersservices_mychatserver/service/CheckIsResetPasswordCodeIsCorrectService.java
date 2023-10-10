package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeData;
import com.example.usersservices_mychatserver.port.in.CheckIsCorrectResetPasswordCodeUseCase;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.service.message.UserErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class CheckIsResetPasswordCodeIsCorrectService implements CheckIsCorrectResetPasswordCodeUseCase {
    private final UserRepositoryPort postgreUserRepository;
    private final ResetPasswordCodeRepositoryPort resetPasswordCodeRepository;
    public CheckIsResetPasswordCodeIsCorrectService(UserRepositoryPort postgreUserRepository, ResetPasswordCodeRepositoryPort resetPasswordCodeRepository) {
        this.postgreUserRepository = postgreUserRepository;
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
    }

    @Override
    public Mono<Result<IsCorrectResetPasswordCode>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono) {
        return emailAndCodeMono.flatMap(emailAndCode -> postgreUserRepository.findUserWithEmail(emailAndCode.email()).
                flatMap(userFromDb -> resetPasswordCodeRepository.findResetPasswordCodeForUser(new IdUserData(userFromDb.id())).flatMap(codeFromDb -> {
                    if (codeFromDb.code().equals(emailAndCode.code())) {
                        return Mono.just(Result.<IsCorrectResetPasswordCode>success(new IsCorrectResetPasswordCode(true)));
                    } else {
                        return Mono.just(Result.<IsCorrectResetPasswordCode>success(new IsCorrectResetPasswordCode(false)));
                    }
                }).switchIfEmpty(Mono.just(Result.<IsCorrectResetPasswordCode>error(UserErrorMessage.USER_NOT_FOUND.getMessage()))))).
                switchIfEmpty(Mono.just(Result.<IsCorrectResetPasswordCode>error(UserErrorMessage.USER_NOT_FOUND.getMessage())));

    }
}
