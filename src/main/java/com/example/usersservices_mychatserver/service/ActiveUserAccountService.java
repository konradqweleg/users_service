package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.ActiveUserAccountPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ActiveUserAccountService implements ActiveUserAccountPort {
    private final CodeVerificationRepositoryPort postgreCodeVerificationRepository;
    private final UserRepositoryPort userRepository;
    private final SendEmailToUserPort sendEmail;
    private final GenerateRandomCodePort generateCode;


    public ActiveUserAccountService(CodeVerificationRepositoryPort postgreCodeVerificationRepository, UserRepositoryPort userRepository, SendEmailToUserPort sendEmail, GenerateRandomCodePort generateCode) {
        this.postgreCodeVerificationRepository = postgreCodeVerificationRepository;
        this.userRepository = userRepository;
        this.sendEmail = sendEmail;
        this.generateCode = generateCode;
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

    @Override
    public Mono<Result<Status>> resendActiveUserAccountCode(Mono<IdUserData> idUserMono) {

        return idUserMono.flatMap(idUserData -> {
            Mono<UserMyChat> userData = userRepository.findUserById(idUserData.idUser());
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
