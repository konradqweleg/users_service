package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
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
public class GetUserAboutIdTests extends BaseTests {

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }

    @Test
    public void testGetUserWithIdUserExist() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);

        Long userId = databaseClient.sql("SELECT id FROM USER_MY_CHAT WHERE email = :email")
                .bind("email", userRegisterData.email())
                .map(row -> row.get("id", Integer.class))
                .one()
                .map(Integer::longValue)
                .block();

        // when
        Mono<UserData> result = userPort.getUserAboutId(new IdUserData(userId));

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
    public void testGetUserWithIdUserDoesNotExist() {

        //when
        long idUserDoesNotExists = 1;
        Mono<UserData> result = userPort.getUserAboutId(new IdUserData(idUserDoesNotExists));

        // then
        StepVerifier.create(result)
                .expectError(UserDoesNotExistsException.class)
                .verify();

    }


}