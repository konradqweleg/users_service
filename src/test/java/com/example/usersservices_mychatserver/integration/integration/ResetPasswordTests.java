package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.integration.integration.dbUtils.DatabaseActionUtilService;
import com.example.usersservices_mychatserver.model.UserMyChat;
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
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ResetPasswordTests {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseActionUtilService databaseActionUtilService;

    @Autowired
    private DatabaseClient databaseClient;

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

    private URI createRequestResetPassword() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/changePassword");
    }


    private URI createRequestActiveUserAccount() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/activeAccount");
    }


    @Test
    public void whenValidResetPasswordCodeAndNewPasswordSentShouldResetPassword() throws URISyntaxException {
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

        UserEmailData userEmailData = new UserEmailData(correctUserRegisterData.email());

        webTestClient.post().uri(createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();



        AtomicReference<String> oldPassword = new AtomicReference<>("");

        Flux<UserMyChat> userDataOldPassword = databaseClient.sql("SELECT id, name,surname,email,password,id_role,is_active_account FROM users_services_scheme.user_my_chat WHERE email = '" + correctUserRegisterData.email() + "'")
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
                .consumeNextWith(user -> oldPassword.set(user.password()))
                .expectComplete()
                .verify();




        //when
        ChangePasswordData resetPasswordData = new ChangePasswordData( correctUserRegisterData.email(),"000000", "new_password");

        webTestClient.post().uri(createRequestResetPassword())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(resetPasswordData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();

        //then
        Flux<UserMyChat> userDataFlux = databaseClient.sql("SELECT id, name,surname,email,password,id_role,is_active_account FROM users_services_scheme.user_my_chat WHERE email = '" + correctUserRegisterData.email() + "'")
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

        StepVerifier.create(userDataFlux)
                .expectNextMatches(user ->
                         !user.password().equals(oldPassword.get())

                )
                .expectComplete()
                .verify();
    }

    @Test
    public void whenInvalidResetPasswordCodeSentShouldFailToResetPassword() throws URISyntaxException {
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

        UserEmailData userEmailData = new UserEmailData(correctUserRegisterData.email());

        webTestClient.post().uri(createRequestSendResetPasswordCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody();




        String badResetPasswordCode = "111111";
        //when
        ChangePasswordData resetPasswordData = new ChangePasswordData( correctUserRegisterData.email(),badResetPasswordCode, "new_password");

        webTestClient.post().uri(createRequestResetPassword())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(resetPasswordData))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody();



    }


}
