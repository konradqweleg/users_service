package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.*;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.SendResetPasswordCodeUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCode;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithResetPasswordCodePort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class SendResetPasswordCodeService implements SendResetPasswordCodeUseCase {
    ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort;
    UserRepositoryPort userRepositoryPort;

    GenerateRandomCode generateRandomCode;

    SendEmailWithResetPasswordCodePort sendEmailWithResetPasswordCodePort;

    SendResetPasswordCodeService(ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort, UserRepositoryPort userRepositoryPort, GenerateRandomCode generateRandomCode, SendEmailWithResetPasswordCodePort sendEmailWithResetPasswordCodePort) {
        this.resetPasswordCodeRepositoryPort = resetPasswordCodeRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.generateRandomCode = generateRandomCode;
        this.sendEmailWithResetPasswordCodePort = sendEmailWithResetPasswordCodePort;
    }

    @Override
    public Mono<Result<Status>> sendResetPasswordCode(Mono<UserEmailData> emailDataMono) {
        return emailDataMono.flatMap(x->{
            Mono<UserMyChat> userData = userRepositoryPort.findUserWithEmail(x.email());
            return userData.map(user->{
                resetPasswordCodeRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(user.id())).subscribeOn(Schedulers.immediate()).subscribe();
                return user;
            }).map(user->{
                String generatedCode = generateRandomCode.generateCode();
                sendEmailWithResetPasswordCodePort.sendResetPasswordCode(user.email(),generatedCode);
                resetPasswordCodeRepositoryPort.insertResetPasswordCode(new ResetPasswordCode(null,user.id(),generatedCode))
                        .subscribeOn(Schedulers.immediate())
                        .subscribe();
                return Result.success(new Status(true));
            }).switchIfEmpty(Mono.just(Result.<Status>error("User not found")));
        });

    }
}
