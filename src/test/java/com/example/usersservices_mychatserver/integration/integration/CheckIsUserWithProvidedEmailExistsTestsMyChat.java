package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.example.usersservices_mychatserver.integration.integration.responseUtil.ResponseMessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

public class CheckIsUserWithProvidedEmailExistsTestsMyChat extends DefaultTestConfiguration  {
    @Test
    public void whenUserExistsRequestShouldReturnInformationUserWithProvidedEmailExists() throws Exception {
        //given
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);
        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsUserWithProvidedEmailExists())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo(true);

    }

    @Test
    public void whenUserNotExistsRequestShouldReturnUserNotExists() throws Exception {
        //given
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);
        UserEmailData noExistsUserEmail = new UserEmailData("noExistsEmail@mail.pl");
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsUserWithProvidedEmailExists())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(noExistsUserEmail))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(ResponseMessageUtil.getUserNotFound());

    }

   @Test
   public void whenUserAccountIsNotActiveRequestShouldReturnInformationAccountNotActive() throws Exception {
        //given
        createUserAccountWithNotActiveAccount(CorrectRequestData.USER_REGISTER_DATA);
        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsUserWithProvidedEmailExists())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(ResponseMessageUtil.getUserAccountNotActive());

    }

    @Test
    public void whenUserEmailIsNullRequestShouldReturnResponseCode400() throws Exception {
        //given
        UserEmailData userEmailData = new UserEmailData(null);
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsUserWithProvidedEmailExists())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().is4xxClientError();

    }

    @Test
    public void whenUserEmailIsNotCorrectFormatRequestShouldReturnResponseCode400() throws Exception {
        //given
        UserEmailData userEmailData = new UserEmailData("notCorrectFormatEmail");
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsUserWithProvidedEmailExists())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().is4xxClientError();

    }

    @Test
    public void whenUserEmailIsEmptyRequestShouldReturnResponseCode400() throws Exception {
        //given
        UserEmailData userEmailData = new UserEmailData("");
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsUserWithProvidedEmailExists())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().is4xxClientError();

    }





}

