package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.config.keycloak.KeyCloakConfiguration;
import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
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
public class ActivateUserAccountTests {

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
    public void testActivateUserAccount_Success() {
        // given
        ActiveAccountCodeData codeData = new ActiveAccountCodeData("123456", "mail@mail.pl");
        UserMyChat user = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
        CodeVerification codeVerification = new CodeVerification(1L, user.id(), "123456");

        when(userRepositoryPort.findUserWithEmail(codeData.email())).thenReturn(Mono.just(user));
        when(userRepositoryPort.findActiveUserAccountCodeForUserWithId(user.id())).thenReturn(Mono.just(codeVerification));
        when(userAuthPort.activateUserAccount(any())).thenReturn(Mono.empty());
        when(userRepositoryPort.deleteUserActiveAccountCode(codeVerification)).thenReturn(Mono.empty());

        // when
        Mono<Result<Status>> result = userPort.activateUserAccount(Mono.just(codeData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isSuccess)
                .expectComplete()
                .verify();
    }

    @Test
    public void testActivateUserAccount_BadCode() {
        // given
        ActiveAccountCodeData codeData = new ActiveAccountCodeData("wrongCode", "mail@mail.pl");
        UserMyChat user = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
        CodeVerification codeVerification = new CodeVerification(1L, user.id(), "123456");

        when(userRepositoryPort.findUserWithEmail(codeData.email())).thenReturn(Mono.just(user));
        when(userRepositoryPort.findActiveUserAccountCodeForUserWithId(user.id())).thenReturn(Mono.just(codeVerification));

        // when
        Mono<Result<Status>> result = userPort.activateUserAccount(Mono.just(codeData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testActivateUserAccount_UserNotFound() {
        // given
        ActiveAccountCodeData codeData = new ActiveAccountCodeData("123456", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(codeData.email())).thenReturn(Mono.empty());

        // when
        Mono<Result<Status>> result = userPort.activateUserAccount(Mono.just(codeData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testActivateUserAccount_CodeNotFound() {
        // given
        ActiveAccountCodeData codeData = new ActiveAccountCodeData("123456", "mail@mail.pl");
        UserMyChat user = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(codeData.email())).thenReturn(Mono.just(user));
        when(userRepositoryPort.findActiveUserAccountCodeForUserWithId(user.id())).thenReturn(Mono.empty());

        // when
        Mono<Result<Status>> result = userPort.activateUserAccount(Mono.just(codeData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testActivateUserAccount_ErrorDuringProcess() {
        // given
        ActiveAccountCodeData codeData = new ActiveAccountCodeData("123456", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(codeData.email())).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // when
        Mono<Result<Status>> result = userPort.activateUserAccount(Mono.just(codeData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }


    @Test
    public void testActivateUserAccount_FindActiveUserAccountCodeFailure() {
        // given
        ActiveAccountCodeData codeData = new ActiveAccountCodeData("123456", "mail@mail.pl");
        UserMyChat user = new UserMyChat(1L, "root", "surname", "mail@mail.pl");

        when(userRepositoryPort.findUserWithEmail(codeData.email())).thenReturn(Mono.just(user));
        when(userRepositoryPort.findActiveUserAccountCodeForUserWithId(user.id())).thenReturn(Mono.error(new RuntimeException("Find active user account code error")));

        // when
        Mono<Result<Status>> result = userPort.activateUserAccount(Mono.just(codeData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testActivateUserAccount_ActivateUserAccountFailure() {
        // given
        ActiveAccountCodeData codeData = new ActiveAccountCodeData("123456", "mail@mail.pl");
        UserMyChat user = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
        CodeVerification codeVerification = new CodeVerification(1L, user.id(), "123456");

        when(userRepositoryPort.findUserWithEmail(codeData.email())).thenReturn(Mono.just(user));
        when(userRepositoryPort.findActiveUserAccountCodeForUserWithId(user.id())).thenReturn(Mono.just(codeVerification));
        when(userAuthPort.activateUserAccount(any())).thenReturn(Mono.error(new RuntimeException("Activate user account error")));

        // when
        Mono<Result<Status>> result = userPort.activateUserAccount(Mono.just(codeData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

}
