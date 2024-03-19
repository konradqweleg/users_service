package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

public class GetUserAboutEmailTest extends DefaultTestConfiguration{
    @Test
    public void whenUserWithProvidedIdNotExistsRequestShouldReturnErrorUserNotFound() throws URISyntaxException {

        //when
        //then
        String noExistsUserEmail = "noExistsUser@email.pl";

        webTestClient.get().uri(createRequestUtil().createRequestGetUserAboutEmail() + noExistsUserEmail)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo("User not found");

    }

    @Test
    public void whenProvidedCorrectEmailUserExistsRequestShouldReturnCorrectResponse() throws URISyntaxException {
        // given
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);

        // when
        //then
        webTestClient.get().uri(createRequestUtil().createRequestGetUserAboutEmail() + CorrectRequestData.USER_REGISTER_DATA.email())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo(CorrectRequestData.USER_REGISTER_DATA.email())
                .jsonPath("$.name").isEqualTo(CorrectRequestData.USER_REGISTER_DATA.name())
                .jsonPath("$.surname").isEqualTo(CorrectRequestData.USER_REGISTER_DATA.surname());
    }


}
