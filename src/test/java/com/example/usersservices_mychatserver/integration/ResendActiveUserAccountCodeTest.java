package com.example.usersservices_mychatserver.integration;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.integration.dbUtils.DatabaseActionUtilService;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final ActiveAccountCodeData CorrectResendIdUserData = new ActiveAccountCodeData("000000", correctUserRegisterData.email());

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
        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctUserRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");


        when(randomCodePort.generateCode()).thenReturn("000000");
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
        //Zamiana ID na maila
//        webTestClient.post().uri(createRequestResendActiveUserAccountCode())
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(correctActiveAccountCodeData))
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.correctResponse").isEqualTo("true");
    }


}
