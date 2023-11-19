package com.example.usersservices_mychatserver.integration;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.LoginAndPasswordData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.integration.dbUtils.DatabaseActionUtilService;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

//Testy na nulle, puste dane, poprawka metody sprawdzania kodu na obsluge złego maila
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LoginTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private DatabaseActionUtilService databaseActionUtilService;

    @LocalServerPort
    private int serverPort;

    private URI createRequestLogin() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/login");
    }

    private URI createRequestRegister() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/register");
    }

    private URI createRequestActiveUserAccount() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/activeAccount");
    }


    @BeforeEach
    public void clearAllDatabaseInDatabaseBeforeRunTest() {
        databaseActionUtilService.clearAllUsersInDatabase();
    }

    @AfterEach
    public void clearAllDataInDatabaseAfterRunTest() {
        databaseActionUtilService.clearAllDataInDatabase();
    }

    @MockBean
    private GenerateRandomCodePort randomCodePort;


    private LoginAndPasswordData userLOginData = new LoginAndPasswordData("correctMail@format.eu", "password");
    private static final UserRegisterData correctUserRegisterData = new UserRegisterData("John", "Walker", "correctMail@format.eu", "password");

    @Test
    public void whenUserWithGivenLoginDoesNotExistSystemShouldReturnError4xx() throws URISyntaxException {

        //when
        //then
        webTestClient.post().uri(createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLOginData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(" User not found ");


    }


    @Test
    public void whenLoginCredentialsAreCorrectButUserAccountIsInactiveSystemShouldReturnError4xx() throws URISyntaxException {

        //given
        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctUserRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");

        //when
        //then
        webTestClient.post().uri(createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLOginData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(" Account not active ");


    }


    @Test
    public void whenLoginCredentialsAreValidAndAccountIsActiveSystemShouldReturnCorrectCredentialsResponse() throws URISyntaxException{

        //given
        when(randomCodePort.generateCode()).thenReturn("000000");

        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctUserRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");


        ActiveAccountCodeData activeAccountCodeData = new ActiveAccountCodeData("000000", correctUserRegisterData.email());

        webTestClient.post().uri(createRequestActiveUserAccount())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(activeAccountCodeData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");

        //when
        //then
        webTestClient.post().uri(createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLOginData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isCorrectCredentials").isEqualTo(true);

    }



}
