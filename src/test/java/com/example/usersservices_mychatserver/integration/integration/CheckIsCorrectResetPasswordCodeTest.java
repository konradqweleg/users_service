package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.example.usersservices_mychatserver.integration.integration.responseUtil.ResponseMessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

public class CheckIsCorrectResetPasswordCodeTest extends DefaultTestConfiguration {
    private static final String CORRECT_RESET_PASSWORD_CODE = "000000";
    private static final String BAD_RESET_PASSWORD_CODE = "777777";

    @Test
    public void whenCorrectRestPasswordCodeRequestShouldReturnCorrectResponse() throws URISyntaxException {

        //given
        when(randomCodePort.generateCode()).thenReturn(CORRECT_RESET_PASSWORD_CODE);
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);

        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());
        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();

        //when
        //then
        UserEmailAndCodeData userEmailAndCodeData = new UserEmailAndCodeData(CorrectRequestData.USER_REGISTER_DATA.email(), CORRECT_RESET_PASSWORD_CODE);

        webTestClient.post().uri(createRequestUtil().createRequestCheckIsCorrectResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailAndCodeData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo(true);


    }

    @Test
    public void whenBadRestPasswordCodeRequestShouldReturnWrongResetPasswordCode() throws URISyntaxException {
        //given
        when(randomCodePort.generateCode()).thenReturn(CORRECT_RESET_PASSWORD_CODE);
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);

        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());

        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();

        //when
        //then
        UserEmailAndCodeData userEmailAndCodeData = new UserEmailAndCodeData(CorrectRequestData.USER_REGISTER_DATA.email(), BAD_RESET_PASSWORD_CODE);
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsCorrectResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailAndCodeData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(ResponseMessageUtil.getWrongResetPasswordCode());

    }

    @Test
    public void whenResetPasswordCodeNotSentRequestShouldReturnErrorResetPasswordCodeNotFound() throws URISyntaxException {
        //given
        when(randomCodePort.generateCode()).thenReturn(CORRECT_RESET_PASSWORD_CODE);
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);

        //when
        //then
        UserEmailAndCodeData userEmailAndCodeData = new UserEmailAndCodeData(CorrectRequestData.USER_REGISTER_DATA.email(), BAD_RESET_PASSWORD_CODE);
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsCorrectResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailAndCodeData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(ResponseMessageUtil.getResetPasswordCodeNotFound());


    }

    @Test
    public void whenUserEmailNotExistInServiceRequestShouldReturnErrorResetPasswordCodeNotFound() throws URISyntaxException {
        //given
        when(randomCodePort.generateCode()).thenReturn(CORRECT_RESET_PASSWORD_CODE);

        //when
        //then
        UserEmailAndCodeData userEmailAndCodeData = new UserEmailAndCodeData(CorrectRequestData.USER_REGISTER_DATA.email(), CORRECT_RESET_PASSWORD_CODE);
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsCorrectResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailAndCodeData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(ResponseMessageUtil.getResetPasswordCodeNotFound());


    }

}
