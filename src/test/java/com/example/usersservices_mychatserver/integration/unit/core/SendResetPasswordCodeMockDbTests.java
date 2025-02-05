package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
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
public class SendResetPasswordCodeMockDbTests {

    @MockBean
    private UserAuthPort userAuthPort;

    @Autowired
    private UserPort userPort;

    @MockBean
    private GenerateRandomCodePort generateRandomCodePort;

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    private static final String USER_RESET_PASSWORD_CODE = "123456";

    @Test
    public void whenDeleteResetPasswordCodeFailureShouldReturnSaveDataInRepositoryException() {
        // given
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(userMyChat.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userMyChat.email())).thenReturn(Mono.just(true));
        when(userRepositoryPort.deleteUserActiveAccountCode(userMyChat.id())).thenReturn(Mono.error(new SaveDataInRepositoryException("Delete code error")));
        when(generateRandomCodePort.generateCode()).thenReturn(USER_RESET_PASSWORD_CODE);
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.error(new SaveDataInRepositoryException("Delete reset password code error")));

        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO(userMyChat.email());
        Mono<Void> result = userPort.sendResetPasswordCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }

    @Test
    public void whenInsertResetPasswordCodeFailureShouldReturnSaveDataInRepositoryException() {
        // given
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(userMyChat.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userMyChat.email())).thenReturn(Mono.just(true));
        when(userRepositoryPort.deleteUserActiveAccountCode(userMyChat.id())).thenReturn(Mono.empty());
        when(generateRandomCodePort.generateCode()).thenReturn(USER_RESET_PASSWORD_CODE);
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
        when(userRepositoryPort.insertResetPasswordCode(any())).thenReturn(Mono.error(new SaveDataInRepositoryException("Save code error")));

        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO(userMyChat.email());
        Mono<Void> result = userPort.sendResetPasswordCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }
}