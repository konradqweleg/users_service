package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.integration.integration.dbUtils.DatabaseActionUtilService;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.example.usersservices_mychatserver.integration.integration.responseUtil.ResponseMessageUtil;
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


public class SendRestPasswordCodeTests  extends DefaultTestConfiguration {

    @Test
    public void whenUserIsRegisteredAndEmailInRequestIsCorrectSystemShouldSendResetPasswordCode() throws URISyntaxException{

        //given
        when(randomCodePort.generateCode()).thenReturn("000000");
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);

        //when
        //then
        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());
        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();

    }

    @Test
    public void whenUserIsNotRegisteredSendResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData userEmailData = new UserEmailData("nonexistent@example.com");
        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenEmptyEmailSentForResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData emptyUserEmailData = new UserEmailData("");
        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(emptyUserEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenNullEmailSentForResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData nullUserEmailData = new UserEmailData(null);
        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(nullUserEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenInvalidEmailFormatSentForResetPasswordCodeShouldFail() throws URISyntaxException {
        UserEmailData invalidUserEmailData = new UserEmailData("invalid_email_format");
        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidUserEmailData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }

    @Test
    public void whenUserAccountIsNotActiveRequestShouldReturnInformationAccountNotActive() throws Exception {
        //given
        createUserAccountWithNotActiveAccount(CorrectRequestData.USER_REGISTER_DATA);
        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(ResponseMessageUtil.getUserAccountNotActive());

    }




}
