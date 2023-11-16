package com.example.usersservices_mychatserver.integration;

import com.example.usersservices_mychatserver.entity.request.UserRegisterData;

import com.example.usersservices_mychatserver.integration.dbUtils.DatabaseActionUtilService;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient


//Check duplicated email
class RegisterUserTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private DatabaseActionUtilService databaseActionUtilService;

    @LocalServerPort
    private int serverPort;

    private URI createRequestRegister() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/register");
    }


    @MockBean
    private SendEmailToUserPort sendEmailToUserPort;

    private static final UserRegisterData correctUserRegisterData = new UserRegisterData("John", "Walker", "correctMail@format.eu", "password");


    @BeforeEach
    public void clearAllDatabaseInDatabaseBeforeRunTest() {
        databaseActionUtilService.clearAllUsersInDatabase();
    }

    @AfterEach
    public void clearAllDataInDatabaseAfterRunTest() {
        databaseActionUtilService.clearAllDataInDatabase();
    }

    @Test
    public void whenUserRegistrationDataIsValidSystemShouldSendActiveAccountCodeToUser() throws URISyntaxException {


        //when
        //then
        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctUserRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");


        Mockito.verify(sendEmailToUserPort, Mockito.times(1)).sendVerificationCode(Mockito.any(), Mockito.anyString());


        Flux<String> userActiveAccountCode = databaseClient.sql("SELECT users_services_scheme.code_verification.code" +
                        " FROM users_services_scheme.code_verification INNER JOIN " +
                        " users_services_scheme.user_my_chat ON  " +
                        "users_services_scheme.user_my_chat.email = '" + correctUserRegisterData.email() + "' ")
                .map((row, metadata) ->
                        row.get("code", String.class)
                )
                .all();

        StepVerifier.create(userActiveAccountCode)
                .expectNextMatches(code -> !code.isEmpty())
                .expectComplete()
                .verify();

        databaseActionUtilService.clearAllDataInDatabase();
    }


    @Test
    public void whenUserRegistrationDataIsValidUserShouldBeCreated() throws URISyntaxException {
        //given
        int USER_ROLE_ID = 1;

        //when
        //then
        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctUserRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");

        Flux<UserMyChat> userDataFlux = databaseClient.sql("SELECT id, name,surname,email,password,id_role,is_active_account FROM users_services_scheme.user_my_chat WHERE email = '" + correctUserRegisterData.email() + "'")
                .map((row, metadata) -> new UserMyChat(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("surname", String.class),
                        row.get("email", String.class),
                        row.get("password", String.class),
                        row.get("id_role", Integer.class),
                        row.get("is_active_account", Boolean.class)
                ))
                .all();

        //check if user is in database
        StepVerifier.create(userDataFlux)
                .expectNextMatches(user -> user.name().equals(correctUserRegisterData.name())
                        && user.surname().equals(correctUserRegisterData.surname())
                        && user.email().equals(correctUserRegisterData.email())
                        && !user.password().isEmpty()
                        && user.idRole() == USER_ROLE_ID
                        && !user.isActiveAccount()
                )
                .expectComplete()
                .verify();


    }


    @Test
    public void IfAnyRegistrationDataElementHasNullValueRequestShouldReturn4xxResponse() throws URISyntaxException {
        //given


        UserRegisterData userRegisterDataNullName = new UserRegisterData(null, "Walker", "correctMail@format.eu", "password");
        UserRegisterData userRegisterDataNullSurname = new UserRegisterData("John", null, "correctMail@format.eu", "password");
        UserRegisterData userRegisterDataNullEmail = new UserRegisterData("John", "Walker", null, "password");
        UserRegisterData userRegisterDataNullPassword = new UserRegisterData("John", "Walker", "correctMail@format.eu", null);

        List<UserRegisterData> usersRegisterDataWithNullElement = List.of(userRegisterDataNullName, userRegisterDataNullSurname, userRegisterDataNullEmail, userRegisterDataNullPassword);

        //when
        //then


        for (UserRegisterData nullRegisterData : usersRegisterDataWithNullElement) {
            webTestClient.post().uri(createRequestRegister())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(nullRegisterData))
                    .exchange()
                    .expectStatus().is4xxClientError()
                    .expectBody();
        }


        Flux<Long> userIdDataFlux = databaseClient.sql("SELECT id FROM users_services_scheme.user_my_chat")
                .map((row, metadata) ->
                        row.get("id", Long.class)
                )
                .all();

        StepVerifier.create(userIdDataFlux)
                .expectNextCount(0)
                .expectComplete()
                .verify();


    }

    @Test
    public void IfAnyRegistrationDataElementHasEmptyValueRequestShouldReturn4xxResponse() throws URISyntaxException {
        //given

        UserRegisterData userRegisterDataNullName = new UserRegisterData("", "Walker", "correctMail@format.eu", "password");
        UserRegisterData userRegisterDataNullSurname = new UserRegisterData("John", "", "correctMail@format.eu", "password");
        UserRegisterData userRegisterDataNullEmail = new UserRegisterData("John", "Walker", "", "password");
        UserRegisterData userRegisterDataNullPassword = new UserRegisterData("John", "Walker", "correctMail@format.eu", "");

        List<UserRegisterData> usersRegistrationDataWithEmptyElements = List.of(userRegisterDataNullName, userRegisterDataNullSurname, userRegisterDataNullEmail, userRegisterDataNullPassword);

        //when
        //then

        for (UserRegisterData nullRegisterData : usersRegistrationDataWithEmptyElements) {
            webTestClient.post().uri(createRequestRegister())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(nullRegisterData))
                    .exchange()
                    .expectStatus().is4xxClientError()
                    .expectBody();
        }

        Flux<Long> userIdDataFlux = databaseClient.sql("SELECT id FROM users_services_scheme.user_my_chat")
                .map((row, metadata) ->
                        row.get("id", Long.class)
                )
                .all();

        StepVerifier.create(userIdDataFlux)
                .expectNextCount(0)
                .expectComplete()
                .verify();


    }


    @Test
    public void IfEmailIsInIncorrectFormatRequestShouldReturn4xx() throws URISyntaxException {
        //given
        UserRegisterData userRegisterDataBadEmailFormat = new UserRegisterData("John", "Walker", "badEmailFormat", "password");

        //when
        //then

        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRegisterDataBadEmailFormat))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody();

        Flux<Long> userFlux = databaseClient.sql("SELECT id FROM users_services_scheme.user_my_chat")
                .map((row, metadata) ->
                        row.get("id", Long.class)
                )
                .all();

        StepVerifier.create(userFlux)
                .expectNextCount(0)
                .expectComplete()
                .verify();


    }

    @Test
    public void IfRequestIsSentWithoutBodyResponseCodeShouldBe4xx() throws URISyntaxException {
        //when
        //then
        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody();

        Flux<Long> userIdFlux = databaseClient.sql("SELECT id FROM users_services_scheme.user_my_chat")
                .map((row, metadata) ->
                        row.get("id", Long.class)
                )
                .all();

        StepVerifier.create(userIdFlux)
                .expectNextCount(0)
                .expectComplete()
                .verify();


    }

}
