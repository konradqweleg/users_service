package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserData;
import com.example.usersservices_mychatserver.exception.get_user.UserDoesNotExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetUserWithEmailTests extends BaseTests {

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }

    @Test
    public void whenCorrectEmailShouldReturnUser() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);

        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO("mail@mail.pl");
        Mono<UserData> result = userPort.getUserAboutEmail(emailData);

        // then
        StepVerifier.create(result)
                .expectNextMatches(userData ->
                        userData.id() != null &&
                                userData.name().equals(userRegisterData.name()) &&
                                userData.surname().equals(userRegisterData.surname()) &&
                                userData.email().equals(userRegisterData.email())
                )
                .verifyComplete();
    }

    @Test
    public void whenWrongEmailShouldReturnExceptionUserDoesNotExistsException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);

        // when
        UserEmailDataDTO wrongEmailData = new UserEmailDataDTO("wrong@mail.pl");
        Mono<UserData> result = userPort.getUserAboutEmail(wrongEmailData);

        // then
        StepVerifier.create(result).expectError(UserDoesNotExistsException.class).verify();

    }
}
