package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResendActiveUserAccountCodeMockDbTests {

    @MockBean
    private UserAuthPort userAuthPort;

    @Autowired
    private UserPort userPort;

    @MockBean
    private GenerateRandomCodePort generateRandomCodePort;

    @MockBean
    private UserRepositoryPort userRepositoryPort;


    @Test
    public void whenDeleteActiveUserAccountCodeFailureShouldReturnExceptionSaveDataInRepositoryException() {
        // given
        UserEmailDataDTO emailData = new UserEmailDataDTO("mail@mail.pl");
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(emailData.email())).thenReturn(Mono.just(false));
        when(userRepositoryPort.deleteUserActiveAccountCode(userMyChat.id())).thenReturn(Mono.error(new SaveDataInRepositoryException("Delete code error")));

        // when
        Mono<Void> result = userPort.resendActiveUserAccountCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }

    @Test
    public void whenSaveVerificationCodeFailureShouldReturnExceptionSaveDataInRepositoryException() {
        // given
        UserEmailDataDTO emailData = new UserEmailDataDTO("mail@mail.pl");
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(emailData.email())).thenReturn(Mono.just(false));
        when(userRepositoryPort.deleteUserActiveAccountCode(userMyChat.id())).thenReturn(Mono.empty());
        when(generateRandomCodePort.generateCode()).thenReturn("123456");
        when(userRepositoryPort.saveVerificationCode(any())).thenReturn(Mono.error(new SaveDataInRepositoryException("Save code error")));

        // when
        Mono<Void> result = userPort.resendActiveUserAccountCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }

    @Test
    public void whenIsActivatedUserAccountFailureShouldReturnExceptionAuthServiceException() {
        // given
        UserEmailDataDTO emailData = new UserEmailDataDTO("mail@mail.pl");
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.error(new AuthServiceException("Is activated user account error")));

        // when
        Mono<Void> result = userPort.resendActiveUserAccountCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(AuthServiceException.class)
                .verify();
    }

    @Test
    public void whenFindUserWithEmailFailureShouldReturnExceptionSaveDataInRepositoryException() {
        // given
        UserEmailDataDTO emailData = new UserEmailDataDTO("mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.error(new SaveDataInRepositoryException("Find user error")));

        // when
        Mono<Void> result = userPort.resendActiveUserAccountCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }
}
