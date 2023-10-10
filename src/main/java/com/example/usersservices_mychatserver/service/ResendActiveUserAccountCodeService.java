package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.ResendActiveUserAccountCodeUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithVerificationCodePort;
import com.example.usersservices_mychatserver.service.message.UserErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ResendActiveUserAccountCodeService implements ResendActiveUserAccountCodeUseCase {
    private final SendEmailWithVerificationCodePort sendEmail;
    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;

    private final UserRepositoryPort userRepositoryPort;
    private final GenerateRandomCodePort generateCode;

    public ResendActiveUserAccountCodeService(SendEmailWithVerificationCodePort sendEmail, CodeVerificationRepositoryPort postgreCodeVerificationRepository, UserRepositoryPort userRepositoryPort, GenerateRandomCodePort generateCode) {
        this.sendEmail = sendEmail;
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
        this.userRepositoryPort = userRepositoryPort;
        this.generateCode = generateCode;
    }


    @Override
    public Mono<Result<Status>> resendActiveUserAccountCode(Mono<IdUserData> idUserMono) {
        return idUserMono.flatMap(idUserData -> {
            Mono<UserMyChat> userData = userRepositoryPort.findUserById(idUserData.idUser());
            return userData.map(userMyChat -> {
                postgreCodeVerificationRepository.deleteUserActivationCode(userMyChat.id()).subscribeOn(Schedulers.immediate()).subscribe();
                return userMyChat;
            }).map(userFromDb-> {
                String generatedCode = generateCode.generateCode();
                sendEmail.sendVerificationCode(userFromDb,generatedCode);
                postgreCodeVerificationRepository.saveVerificationCode(new CodeVerification(null,userFromDb.id(),generatedCode))
                        .subscribeOn(Schedulers.immediate())
                        .subscribe();
               return Result.success(new Status(true));

            }  ).switchIfEmpty(Mono.just(Result.<Status>error(UserErrorMessage.USER_NOT_FOUND.getMessage())));
        });



    }
}
