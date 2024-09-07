package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.config.keycloak.KeyCloakConfiguration;
import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
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
public class ChangeUserPasswordTests {

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
    public void testChangeUserPassword_Success() {
        // given
        ChangePasswordData changePasswordData = new ChangePasswordData("mail@mail.pl", "code123", "newPassword");
        UserMyChat userFromDb = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
        ResetPasswordCode resetPasswordCode = new ResetPasswordCode(1L, userFromDb.id(), "code123");

        when(userRepositoryPort.findUserWithEmail(changePasswordData.email())).thenReturn(Mono.just(userFromDb));
        when(userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))).thenReturn(Mono.just(resetPasswordCode));
        when(userAuthPort.changeUserPassword(any(),any())).thenReturn(Mono.empty());
        when(userRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(userFromDb.id()))).thenReturn(Mono.empty());

        // when
        Mono<Result<Status>> result = userPort.changeUserPassword(Mono.just(changePasswordData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isSuccess)
                .expectComplete()
                .verify();
    }

    @Test
    public void testChangeUserPassword_WrongCode() {
        // given
        ChangePasswordData changePasswordData = new ChangePasswordData("mail@mail.pl", "wrongCode", "newPassword");
        UserMyChat userFromDb = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
        ResetPasswordCode resetPasswordCode = new ResetPasswordCode(1L, userFromDb.id(), "code123");

        when(userRepositoryPort.findUserWithEmail(changePasswordData.email())).thenReturn(Mono.just(userFromDb));
        when(userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))).thenReturn(Mono.just(resetPasswordCode));

        // when
        Mono<Result<Status>> result = userPort.changeUserPassword(Mono.just(changePasswordData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testChangeUserPassword_UserNotFound() {
        // given
        ChangePasswordData changePasswordData = new ChangePasswordData("mail@mail.pl", "code123", "newPassword");

        when(userRepositoryPort.findUserWithEmail(changePasswordData.email())).thenReturn(Mono.empty());

        // when
        Mono<Result<Status>> result = userPort.changeUserPassword(Mono.just(changePasswordData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testChangeUserPassword_RuntimeException() {
        // given
        ChangePasswordData changePasswordData = new ChangePasswordData("mail@mail.pl", "code123", "newPassword");

        when(userRepositoryPort.findUserWithEmail(changePasswordData.email())).thenReturn(Mono.error(new RuntimeException("Runtime exception")));

        // when
        Mono<Result<Status>> result = userPort.changeUserPassword(Mono.just(changePasswordData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void testChangeUserPassword_AuthPortChangePasswordFailure() {
        // given
        ChangePasswordData changePasswordData = new ChangePasswordData("mail@mail.pl", "code123", "newPassword");
        UserMyChat userFromDb = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
        ResetPasswordCode resetPasswordCode = new ResetPasswordCode(1L, userFromDb.id(), "code123");

        when(userRepositoryPort.findUserWithEmail(changePasswordData.email())).thenReturn(Mono.just(userFromDb));
        when(userRepositoryPort.findResetPasswordCodeForUserById(new IdUserData(userFromDb.id()))).thenReturn(Mono.just(resetPasswordCode));
        when(userAuthPort.changeUserPassword(any(), any())).thenReturn(Mono.error(new RuntimeException("Change password error")));

        // when
        Mono<Result<Status>> result = userPort.changeUserPassword(Mono.just(changePasswordData));

        // then
        StepVerifier.create(result)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }
}