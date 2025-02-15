package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.response.UserData;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetUserMatchingNameAndSurnameMockDbTests {

    @Autowired
    private UserPort userPort;

    @MockBean
    private UserRepositoryPort userRepositoryPort;


    @Test
    public void whenFindUserMatchingNameAndSurnameFailureShouldReturnSaveDataInRepositoryException() {
        // given
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserMatchingNameAndSurname(userMyChat.name(), userMyChat.surname()))
                .thenReturn(Flux.error(new SaveDataInRepositoryException("Find user error")));

        // when
        Flux<UserData> result = userPort.getUserMatchingNameAndSurname(userMyChat.name() + " " + userMyChat.surname());

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }

    @Test
    public void whenFindUserMatchingNameOrSurnameFailureShouldReturnSaveDataInRepositoryException() {
        // given
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserMatchingNameOrSurname(userMyChat.name(), userMyChat.name()))
                .thenReturn(Flux.error(new SaveDataInRepositoryException("Find user error")));

        // when
        Flux<UserData> result = userPort.getUserMatchingNameAndSurname(userMyChat.name());

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }

}
