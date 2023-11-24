package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserLoginData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.integration.integration.dbUtils.DatabaseActionUtilService;
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
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResendActiveUserAccountCodeTest {

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

    private URI createRequestActiveUserAccount() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/activeAccount");
    }


    @Test
    public void whenUserAccountHasBeenActivatedRequestShouldReturn4xxError() throws URISyntaxException {
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
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctResendActiveAccountCode))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();

    }

    @Test
    public void whenUserFillRegisterDataButNotActivateAccountResendActiveAccountCodeShouldSendCode() throws URISyntaxException {
        //given
        String generatedCode = "123456";


        when(randomCodePort.generateCode()).thenReturn(generatedCode);


        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctUserRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");



        UserLoginData userLoginData = new UserLoginData(correctUserRegisterData.email());


        //when
        //then
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLoginData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");
    }


    @Test
    public void whenUserDidNotRegisterResendActiveAccountCodeShouldFail() throws URISyntaxException {
        //when
        //then
        UserLoginData userLoginData = new UserLoginData("nonexistent@example.com");
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenEmptyOrNullDataSentResendActiveAccountCodeShouldFail() throws URISyntaxException {

        //given
        when(randomCodePort.generateCode()).thenReturn("000000");

        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctUserRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");


        //when
        //then
        UserLoginData emptyUserLoginData = new UserLoginData("");
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(emptyUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();


        UserLoginData nullUserLoginData = new UserLoginData(null);
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(nullUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }


    @Test
    public void whenUserDidNotRegisterAndResendActiveAccountCodeWithInvalidEmailFormatShouldFail() throws URISyntaxException {
        //when
        UserLoginData invalidUserLoginData = new UserLoginData("invalid_email_format");
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenUserDidNotRegisterAndResendActiveAccountCodeWithValidEmailFormatShouldFail() throws URISyntaxException {
        //when
        UserLoginData validUserLoginData = new UserLoginData("valid_email@example.com");
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(validUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenUserDidNotRegisterAndResendActiveAccountCodeWithEmptyDataShouldFail() throws URISyntaxException {
        //when
        UserLoginData emptyUserLoginData = new UserLoginData("");
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(emptyUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenUserDidNotRegisterAndResendActiveAccountCodeWithNullDataShouldFail() throws URISyntaxException {
        //when
        UserLoginData nullUserLoginData = new UserLoginData(null);
        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(nullUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }




}
