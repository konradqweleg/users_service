package com.example.usersservices_mychatserver.integration;

import com.example.usersservices_mychatserver.entity.request.UserRegisterData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

class RegisterUserTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private  DatabaseClient databaseClient;


    @LocalServerPort
    private int serverPort;

    @Test
    void contextLoads() {
    }

    private URI createRequestRegister() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/register");
    }


    public void clearAllUsers() {
        databaseClient.sql("DELETE FROM users_services_scheme.user_my_chat where 1=1").
                fetch().
                rowsUpdated()
                .block();
    }

    @Test
    public void whenCorrectNewUserRegisterDataUserIsRegisteredCorrectly() throws URISyntaxException {
        //given
        clearAllUsers();
        UserRegisterData userRegisterData = new UserRegisterData("Jan", "Kowalski", "email@email.p2l","password");

        //when
        //then
        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");

        Flux<UserRegisterData> userFlux = databaseClient.sql("SELECT name,surname,email,password FROM users_services_scheme.user_my_chat WHERE name = 'Jan' AND surname = 'Kowalski' ")
                .map((row, metadata) -> new UserRegisterData(
                        row.get("name", String.class),
                        row.get("surname", String.class),
                        row.get("email", String.class),
                        row.get("password", String.class)
                ))
                .all();

        StepVerifier.create(userFlux)
                .expectNextMatches(user -> user.name().equals("Jan") && user.surname().equals("Kowalski") && user.email().equals("email@email.p2l"))
                .expectComplete()
                .verify();


        clearAllUsers();
    }


    @Test
    public void ifAnyPartOfTheUserRegistrationDataIsNullReturnError() throws URISyntaxException {
        //given
        clearAllUsers();
        UserRegisterData userRegisterDataNullName = new UserRegisterData(null, "Kowalski", "email@email.p2l", "password");
        UserRegisterData userRegisterDataNullSurname = new UserRegisterData("Jan", null, "email@email.p2l", "password");
        UserRegisterData userRegisterDataNullEmail = new UserRegisterData("Jan", "Kowalski", null, "password");
        UserRegisterData userRegisterDataNullPassword = new UserRegisterData("Jan", "Kowalski", "email@email.p2l", null);

        List<UserRegisterData> userRegisterDataEmptyPartOfDataList = List.of(userRegisterDataNullName, userRegisterDataNullSurname, userRegisterDataNullEmail, userRegisterDataNullPassword);

        //when
        //then
        for(UserRegisterData nullRegisterData : userRegisterDataEmptyPartOfDataList){
            webTestClient.post().uri(createRequestRegister())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(nullRegisterData))
                    .exchange()
                    .expectStatus().is4xxClientError()
                    .expectBody();
        }

    }

    @Test
    public void ifAnyPartOfTheUserRegistrationDataIsEmptyReturnError() throws URISyntaxException {
        //given
        clearAllUsers();
        UserRegisterData userRegisterDataNullName = new UserRegisterData("", "Kowalski", "email@email.p2l", "password");
        UserRegisterData userRegisterDataNullSurname = new UserRegisterData("Jan", "", "email@email.p2l", "password");
        UserRegisterData userRegisterDataNullEmail = new UserRegisterData("Jan", "Kowalski", "", "password");
        UserRegisterData userRegisterDataNullPassword = new UserRegisterData("Jan", "Kowalski", "email@email.p2l", "");

        List<UserRegisterData> userRegisterDataEmptyPartOfDataList = List.of(userRegisterDataNullName, userRegisterDataNullSurname, userRegisterDataNullEmail, userRegisterDataNullPassword);

        //when
        //then
        for(UserRegisterData nullRegisterData : userRegisterDataEmptyPartOfDataList){
            webTestClient.post().uri(createRequestRegister())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(nullRegisterData))
                    .exchange()
                    .expectStatus().is4xxClientError()
                    .expectBody();
        }

    }


}
