package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.config.keycloak.KeyCloakConfiguration;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendResetPasswordCodeTests {

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    GenerateRandomCodePort generateCode;

    @MockBean
    UserAuthPort userAuthPort;

    @Autowired
    UserPort userPort;

    @MockBean
    SendEmailToUserPort sendEmailPort;

    @MockBean
    private Keycloak keycloak;

    @MockBean
    private KeyCloakConfiguration keyCloakConfiguration;

    @Mock
    private GenerateRandomCodePort generateRandomCodePort;


    @Test
    public void testSendResetPasswordCode_Success() {
        // given
        UserEmailData emailData = new UserEmailData("mail@mail.pl");
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
        String generatedCode = "123456";

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(true));
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
        when(generateRandomCodePort.generateCode()).thenReturn(generatedCode);
        when(userRepositoryPort.insertResetPasswordCode(any())).thenReturn(Mono.empty());

        // when
        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isSuccess)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSendResetPasswordCode_UserNotActivated() {
        // given
        UserEmailData emailData = new UserEmailData("mail@mail.pl");
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(false));

        // when
        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSendResetPasswordCode_UserNotFound() {
        // given
        UserEmailData emailData = new UserEmailData("mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.empty());

        // when
        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSendResetPasswordCode_ErrorDuringProcess() {
        // given
        UserEmailData emailData = new UserEmailData("mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // when
        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSendResetPasswordCode_DeleteResetPasswordCodeFailure() {
        // given
        UserEmailData emailData = new UserEmailData("mail@mail.pl");
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(true));
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.error(new RuntimeException("Delete reset password code error")));

        // when
        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSendResetPasswordCode_InsertResetPasswordCodeFailure() {
        // given
        UserEmailData emailData = new UserEmailData("mail@mail.pl");
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");


        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(true));
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
        when(userRepositoryPort.insertResetPasswordCode(any())).thenReturn(Mono.error(new RuntimeException("Insert reset password code error")));

        // when
        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }



}
