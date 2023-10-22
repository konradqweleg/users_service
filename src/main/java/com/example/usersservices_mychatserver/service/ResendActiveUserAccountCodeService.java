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
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
            return userData.flatMap(userMyChat -> postgreCodeVerificationRepository.deleteUserActivationCode(userMyChat.id()).
                            thenReturn(userMyChat)).
                    flatMap(userFromDb -> {
                        String generatedCode = generateCode.generateCode();
                        sendEmail.sendVerificationCode(userFromDb, generatedCode);
                        return postgreCodeVerificationRepository.saveVerificationCode(new CodeVerification(null, userFromDb.id(), generatedCode))
                                .thenReturn(Result.success(new Status(true)));
                    }).switchIfEmpty(Mono.just(Result.<Status>error(ErrorMessage.USER_NOT_FOUND.getMessage())));
        }).onErrorResume(ex -> Mono.just(Result.<Status>error(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));


    }
}
