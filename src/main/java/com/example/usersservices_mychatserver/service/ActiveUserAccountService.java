package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.in.ActivateUserAccountUseCase;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
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
    public Mono<Boolean> activateUserAccount(Mono<CodeVerification> codeVerificationMono) {
          return codeVerificationMono.flatMap(
                codeVerificationProvidedByUser -> postgreCodeVerificationRepository.findUserActiveAccountCodeById(codeVerificationProvidedByUser.idUser()).flatMap(
                        codeVerificationSaved -> {
                            if(codeVerificationSaved.code().equals(codeVerificationProvidedByUser.code())){
                                return userRepository.activeUserAccount(codeVerificationProvidedByUser.idUser()).
                                        then(Mono.defer(() -> postgreCodeVerificationRepository.deleteUserActivationCode(codeVerificationSaved).
                                                thenReturn(true)));
                            }else{
                                return Mono.just(false);
                            }
                        }
                )

        );

    }
}
