package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.ResetPasswordCodePort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ResetPasswordCodeService implements ResetPasswordCodePort {
    private final ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    private final GenerateRandomCodePort generateRandomCodePort;

    private final SendEmailToUserPort sendEmailToUserPort;

    public ResetPasswordCodeService(ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort, UserRepositoryPort userRepositoryPort, GenerateRandomCodePort generateRandomCodePort, SendEmailToUserPort sendEmailToUserPort) {
        this.resetPasswordCodeRepositoryPort = resetPasswordCodeRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.generateRandomCodePort = generateRandomCodePort;
        this.sendEmailToUserPort = sendEmailToUserPort;
    }

    @Override
    public Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono) {

        return emailDataMono.flatMap(emailData -> {
            Mono<UserMyChat> userData = userRepositoryPort.findUserWithEmail(emailData.email());
            return userData.flatMap(user -> resetPasswordCodeRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(user.id())).
                            thenReturn(user)).
                    flatMap(user -> {
                        String generatedCode = generateRandomCodePort.generateCode();
                        sendEmailToUserPort.sendResetPasswordCode(user.email(), generatedCode);
                        return resetPasswordCodeRepositoryPort.insertResetPasswordCode(new ResetPasswordCode(null, user.id(), generatedCode)).
                                thenReturn(Result.success(new Status(true)));
                    }).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())));
        }).onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

    }

    @Override
    public Mono<Result<IsCorrectResetPasswordCode>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCodeData> emailAndCodeMono) {
        return emailAndCodeMono.flatMap(emailAndCode -> userRepositoryPort.findUserWithEmail(emailAndCode.email()).
                        flatMap(userFromDb -> resetPasswordCodeRepositoryPort.findResetPasswordCodeForUser(new IdUserData(userFromDb.id())).flatMap(codeFromDb -> {
                            if (codeFromDb.code().equals(emailAndCode.code())) {
                                return Mono.just(Result.<IsCorrectResetPasswordCode>success(new IsCorrectResetPasswordCode(true)));
                            } else {
                                return Mono.just(Result.<IsCorrectResetPasswordCode>success(new IsCorrectResetPasswordCode(false)));
                            }
                        }).switchIfEmpty(Mono.just(Result.<IsCorrectResetPasswordCode>error(ErrorMessage.USER_NOT_FOUND.getMessage())))))
                .onErrorResume(RuntimeException.class,ex -> Mono.just(Result.<IsCorrectResetPasswordCode>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));


    }
}
