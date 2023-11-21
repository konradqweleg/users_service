package com.example.usersservices_mychatserver.integration;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.integration.dbUtils.DatabaseActionUtilService;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class SendRestPasswordCodeTests {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseActionUtilService databaseActionUtilService;

    @MockBean
    private GenerateRandomCodePort randomCodePort;

    @BeforeEach
    public void clearAllDatabaseInDatabaseBeforeRunTest() {
        databaseActionUtilService.clearAllDataInDatabase();
    }

    @AfterEach
    public void clearAllDataInDatabaseAfterRunTest() {
        databaseActionUtilService.clearAllDataInDatabase();
    }

    private static final UserRegisterData correctUserRegisterData = new UserRegisterData("John", "Walker", "correctMail@format.eu", "password");

    private static final ActiveAccountCodeData correctResendActiveAccountCode = new ActiveAccountCodeData("000000", correctUserRegisterData.email());

    private URI createRequestResendActiveUserAccountCode() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/activeAccount/resendCode");
    }
    private URI createRequestRegister() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/register");
    }

    private URI createRequestSendResetPasswordCode() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/resetPasswordCode/sendCode");
    }


    private URI createRequestActiveUserAccount() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/activeAccount");
    }

    @Test
    public void whenUserIsRegisteredAndEmailInRequestIsCorrectSystemShouldSendResetPasswordCode() throws URISyntaxException{

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

        UserEmailData userEmailData = new UserEmailData(correctUserRegisterData.email());

        webTestClient.post().uri(createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();


    }

    @Test
    public void whenUserIsNotRegisteredSendResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData userEmailData = new UserEmailData("nonexistent@example.com");
        webTestClient.post().uri(createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenEmptyEmailSentForResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData emptyUserEmailData = new UserEmailData("");
        webTestClient.post().uri(createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(emptyUserEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenNullEmailSentForResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData nullUserEmailData = new UserEmailData(null);
        webTestClient.post().uri(createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(nullUserEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenInvalidEmailFormatSentForResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData invalidUserEmailData = new UserEmailData("invalid_email_format");
        webTestClient.post().uri(createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidUserEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }




}
