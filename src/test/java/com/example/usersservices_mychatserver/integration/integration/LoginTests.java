package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.EmailAndPasswordData;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import java.net.URISyntaxException;

public class LoginTests extends DefaultTestConfiguration {

    private static final String RESPONSE_NOT_AVAILABLE = " Response not available ";
    private static final String USER_NOT_FOUND = " User not found ";

    private static final String USER_ACCOUNT_NOT_ACTIVE = " Account not active ";

    @Test
    public void whenUserEmailIsNullMethodRequestShouldReturnError() throws URISyntaxException {

        //given
        EmailAndPasswordData userLoginDataWithNullEmail = new EmailAndPasswordData(null, "password");
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLoginDataWithNullEmail))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(RESPONSE_NOT_AVAILABLE);

    }

    @Test
    public void whenUserPasswordIsNullMethodRequestShouldReturnError() throws URISyntaxException {

        //given
        EmailAndPasswordData userLoginDataWithNullPassword = new EmailAndPasswordData("correctMail@format.eu", null);

        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLoginDataWithNullPassword))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(RESPONSE_NOT_AVAILABLE);

    }


    @Test
    public void whenUserWithGivenLoginDoesNotExistSystemShouldReturnError4xx() throws URISyntaxException {

        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(CorrectRequestData.USER_LOGIN_DATA))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(USER_NOT_FOUND);


    }


    @Test
    public void whenLoginCredentialsAreCorrectButUserAccountIsInactiveMethodShouldReturnError4xx() throws URISyntaxException {

        //given
        createUserAccountWithNotActiveAccount();

        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(CorrectRequestData.USER_LOGIN_DATA))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(USER_ACCOUNT_NOT_ACTIVE);


    }


    @Test
    public void whenLoginCredentialsAreValidAndAccountIsActiveSystemShouldReturnCorrectCredentialsResponse() throws URISyntaxException {

        //given
        createActivatedUserAccount();

        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(CorrectRequestData.USER_LOGIN_DATA))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isCorrectCredentials").isEqualTo(true);

    }

    @Test
    public void whenUserExistsButLoginDataContainsWrongPasswordSystemShouldReturnLoginCredentialsError() throws URISyntaxException {

        //given
        createActivatedUserAccount();
        EmailAndPasswordData userLoginDataWithBadPassword = new EmailAndPasswordData(CorrectRequestData.USER_REGISTER_DATA.email(), "badPassword");

        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userLoginDataWithBadPassword))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isCorrectCredentials").isEqualTo(false);


    }

    @Test
    public void whenInvalidEmailFormatShouldReturnError4xx() throws URISyntaxException {
        //given
        EmailAndPasswordData invalidEmailData = new EmailAndPasswordData("wrong_mail_format", "password");

        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidEmailData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo(RESPONSE_NOT_AVAILABLE);
    }


}
