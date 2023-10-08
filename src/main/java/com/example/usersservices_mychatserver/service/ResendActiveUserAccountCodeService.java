package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.ActiveUserAccountDataResponse;
import com.example.usersservices_mychatserver.entity.IdUserData;
import com.example.usersservices_mychatserver.entity.ResendUserActiveAccountCodeDataResponse;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.ResendActiveUserAccountCodeUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCode;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithVerificationCodePort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ResendActiveUserAccountCodeService implements ResendActiveUserAccountCodeUseCase {
    private final SendEmailWithVerificationCodePort sendEmail;
    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;

    private final UserRepositoryPort userRepositoryPort;
    private final GenerateRandomCode generateCode;

    public ResendActiveUserAccountCodeService(SendEmailWithVerificationCodePort sendEmail, CodeVerificationRepositoryPort postgreCodeVerificationRepository, UserRepositoryPort userRepositoryPort, GenerateRandomCode generateCode) {
        this.sendEmail = sendEmail;
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
        this.userRepositoryPort = userRepositoryPort;
        this.generateCode = generateCode;
    }


    @Override
    public Mono<Result<ResendUserActiveAccountCodeDataResponse>> resendActiveUserAccountCode(Mono<IdUserData> idUserMono) {
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
               return Result.success(new ResendUserActiveAccountCodeDataResponse(true));

            }  ).switchIfEmpty(Mono.just(Result.<ResendUserActiveAccountCodeDataResponse>error("User not found")));
        });



    }
}
