package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserLoginData;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

public class ResendActiveUserAccountCodeTest  extends  DefaultTestConfiguration{

    private static final ActiveAccountCodeData correctResendActiveAccountCode = new ActiveAccountCodeData("000000", CorrectRequestData.USER_REGISTER_DATA.email());

    @Test
    public void whenUserAccountHasBeenActivatedRequestResendActiveAccountShouldReturn4xxError() throws URISyntaxException {
        //given
        String activeAccountCode = "000000";

        when(randomCodePort.generateCode()).thenReturn(activeAccountCode);
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);


        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(correctResendActiveAccountCode))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();

    }

    @Test
    public void whenUserFillRegisterDataButNotActivateAccountResendActiveAccountCodeShouldSendCode() throws URISyntaxException {
        //given
        createUserAccountWithNotActiveAccount(CorrectRequestData.USER_REGISTER_DATA);
        UserLoginData userResendActiveAccountRequestData = new UserLoginData(CorrectRequestData.USER_REGISTER_DATA.email());

        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userResendActiveAccountRequestData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");
    }


    @Test
    public void whenUserDidNotRegisterResendActiveAccountCodeShouldFail() throws URISyntaxException {
        //when
        //then
        UserLoginData userResendActiveCodeNoExsistsUserRequestData = new UserLoginData("nonexistent@example.com");
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userResendActiveCodeNoExsistsUserRequestData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }




    @Test
    public void whenEmptyResendActiveAccountCodeDataSentResendActiveAccountCodeShouldFail() throws URISyntaxException {

        //given
        createUserAccountWithNotActiveAccount(CorrectRequestData.USER_REGISTER_DATA);

        //when
        UserLoginData emptyUserLoginData = new UserLoginData("");
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(emptyUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();

    }

    @Test
    public void whenNullDataSentResendActiveAccountCodeShouldFail() throws URISyntaxException {

        //given
        createUserAccountWithNotActiveAccount(CorrectRequestData.USER_REGISTER_DATA);
        //when
        //then
        UserLoginData nullUserLoginData = new UserLoginData(null);
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
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
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
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
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
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
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
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
        webTestClient.post().uri(createRequestUtil().createRequestResendActiveUserAccountCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(nullUserLoginData))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody();
    }




}
