package com.example.usersservices_mychatserver.integration.integration;


import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.example.usersservices_mychatserver.model.UserMyChat;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.when;



public class ResetPasswordTests extends DefaultTestConfiguration {
    @Test
    public void whenValidResetPasswordCodeAndNewPasswordSentShouldResetPassword() throws URISyntaxException {
        //given
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);
        AtomicReference<String> oldUserPassword = new AtomicReference<>("");
        String sqlGetUserWithEmail = "SELECT id, name,surname,email,password,id_role,is_active_account FROM users_services_scheme.user_my_chat WHERE email = '" + CorrectRequestData.USER_REGISTER_DATA.email() + "'";

        Flux<UserMyChat> userDataOldPassword = databaseClient.sql(sqlGetUserWithEmail)
                .map((row, metadata) -> new UserMyChat(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("surname", String.class),
                        row.get("email", String.class),
                        row.get("password", String.class),
                        row.get("id_role", Integer.class),
                        row.get("is_active_account", Boolean.class)
                ))
                .all();

        StepVerifier.create(userDataOldPassword)
                .consumeNextWith(user -> oldUserPassword.set(user.password()))
                .expectComplete()
                .verify();


        when(randomCodePort.generateCode()).thenReturn("000000");
        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());

        webTestClient.post().uri(createRequestUtil().createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();
        ChangePasswordData resetPasswordData = new ChangePasswordData( CorrectRequestData.USER_REGISTER_DATA.email(),"000000", "new_password");


        //when
        webTestClient.post().uri(createRequestUtil().createRequestResetPassword())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(resetPasswordData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();

        //then
        Flux<UserMyChat> userWithNewPassword = databaseClient.sql(sqlGetUserWithEmail)
                .map((row, metadata) -> new UserMyChat(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("surname", String.class),
                        row.get("email", String.class),
                        row.get("password", String.class),
                        row.get("id_role", Integer.class),
                        row.get("is_active_account", Boolean.class)
                ))
                .all();

        StepVerifier.create(userWithNewPassword)
                .expectNextMatches(user ->
                         !user.password().equals(oldUserPassword.get())

                )
                .expectComplete()
                .verify();
    }

    @Test
    public void whenInvalidResetPasswordCodeRequestShouldFail() throws URISyntaxException {
        //given
        when(randomCodePort.generateCode()).thenReturn("000000");
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
        String badResetPasswordCode = "111111";
        ChangePasswordData resetPasswordData = new ChangePasswordData( CorrectRequestData.USER_REGISTER_DATA.email(),badResetPasswordCode, "new_password");
        webTestClient.post().uri(createRequestUtil().createRequestResetPassword())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(resetPasswordData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody();


    }


}
