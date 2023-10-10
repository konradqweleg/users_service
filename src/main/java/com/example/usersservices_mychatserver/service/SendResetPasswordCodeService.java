package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.SendResetPasswordCodeUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithResetPasswordCodePort;
import com.example.usersservices_mychatserver.service.message.UserErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class SendResetPasswordCodeService implements SendResetPasswordCodeUseCase {
    ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort;
    UserRepositoryPort userRepositoryPort;

    GenerateRandomCodePort generateRandomCodePort;

    SendEmailWithResetPasswordCodePort sendEmailWithResetPasswordCodePort;

    SendResetPasswordCodeService(ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort, UserRepositoryPort userRepositoryPort, GenerateRandomCodePort generateRandomCodePort, SendEmailWithResetPasswordCodePort sendEmailWithResetPasswordCodePort) {
        this.resetPasswordCodeRepositoryPort = resetPasswordCodeRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.generateRandomCodePort = generateRandomCodePort;
        this.sendEmailWithResetPasswordCodePort = sendEmailWithResetPasswordCodePort;
    }

    @Override
    public Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono) {
        return emailDataMono.flatMap(emailData->{
            Mono<UserMyChat> userData = userRepositoryPort.findUserWithEmail(emailData.email());
            return userData.map(user->{
                resetPasswordCodeRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(user.id())).subscribeOn(Schedulers.immediate()).subscribe();
                return user;
            }).map(user->{
                String generatedCode = generateRandomCodePort.generateCode();
                sendEmailWithResetPasswordCodePort.sendResetPasswordCode(user.email(),generatedCode);
                resetPasswordCodeRepositoryPort.insertResetPasswordCode(new ResetPasswordCode(null,user.id(),generatedCode))
                        .subscribeOn(Schedulers.immediate())
                        .subscribe();
                return Result.success(new Status(true));
            }).switchIfEmpty(Mono.just(Result.<Status>error(UserErrorMessage.USER_NOT_FOUND.getMessage())));
        });

    }
}
