package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeDTO;
import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckIsCorrectResetPasswordCodeMockDbTests {

    @Autowired
    private UserPort userPort;


    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @Test
    public void whenFindUserWithEmailFailureShouldReturnSaveDataInRepositoryException() {
        // given
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(userMyChat.email())).thenReturn(Mono.error(new SaveDataInRepositoryException("Find user error")));


        // when
        UserEmailAndCodeDTO emailAndCode = new UserEmailAndCodeDTO(userMyChat.email(),"123456");
        Mono<IsCorrectResetPasswordCode> result = userPort.checkIsCorrectResetPasswordCode(emailAndCode);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();

    }

    @Test
    public void whenFindResetPasswordCodeFailureShouldReturnSaveDataInRepositoryException() {
        // given
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(userMyChat.email())).thenReturn(Mono.just(userMyChat));
        when(userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userMyChat.id()))).thenReturn(Mono.error(new SaveDataInRepositoryException("Find reset password code error")));


        // when
        UserEmailAndCodeDTO emailAndCode = new UserEmailAndCodeDTO(userMyChat.email(),"123456");
        Mono<IsCorrectResetPasswordCode> result = userPort.checkIsCorrectResetPasswordCode(emailAndCode);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();

    }
}
