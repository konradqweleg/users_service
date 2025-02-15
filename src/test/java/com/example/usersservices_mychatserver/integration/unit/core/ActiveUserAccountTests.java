package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.activation.ActivationCodeNotFoundException;
import com.example.usersservices_mychatserver.exception.activation.BadActiveAccountCodeException;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActiveUserAccountTests extends BaseTests {


    private static final String SQL_GET_USER_ID = "SELECT id FROM user_my_chat WHERE email = :email";
    private static final String SQL_CHECK_CODE = "SELECT COUNT(*) AS count FROM code_verification WHERE id_user = :userId";


    @Autowired
    private DatabaseClient databaseClient;


    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }


    public Mono<Boolean> isActivationCodePresentForUser(String email) {
        return databaseClient.sql(SQL_GET_USER_ID)
                .bind("email", email)
                .map((row, metadata) -> row.get("id", Integer.class))
                .one()
                .flatMap(userId -> databaseClient.sql(SQL_CHECK_CODE)
                        .bind("userId", userId)
                        .map((row, metadata) -> row.get("count", Long.class) > 0)
                        .one());
    }

    @Test
    public void whenCorrectActiveAccountCodeProvidedAccountShouldBeActivated() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.empty());

        String verificationCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);

        userPort.registerUser(userRegisterData).block();

        // when
        Mono<Void> activeAccount = userPort.activateUserAccount(new ActiveAccountCodeData(verificationCode, userRegisterData.email()));

        // then
        StepVerifier.create(activeAccount)
                .expectComplete()
                .verify();

        StepVerifier.create(isActivationCodePresentForUser(userRegisterData.email()))
                .expectNext(false)
                .verifyComplete();

        Mockito.verify(userAuthPort, Mockito.times(1)).activateUserAccount(userRegisterData.email());
    }

    @Test
    public void whenIncorrectActiveAccountCodeProvidedAccountShouldNotBeActivated() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.empty());

        String verificationCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);

        userPort.registerUser(userRegisterData).block();

        // when
        String badVerificationCode = "654321";
        Mono<Void> activateUserAccount = userPort.activateUserAccount(new ActiveAccountCodeData(badVerificationCode, userRegisterData.email()));

        // then
        StepVerifier.create(activateUserAccount)
                .expectError(BadActiveAccountCodeException.class)
                .verify();

        StepVerifier.create(isActivationCodePresentForUser(userRegisterData.email()))
                .expectNext(true)
                .verifyComplete();

        Mockito.verify(userAuthPort, Mockito.times(0)).activateUserAccount(userRegisterData.email());
    }

    @Test
    public void whenUserNotFoundForActivationMethodsShouldReturnExceptionActivationCodeNotFound() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.empty());

        String verificationCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);

        // when
        String badVerificationCode = "654321";
        Mono<Void> activateUserAccount = userPort.activateUserAccount(new ActiveAccountCodeData(badVerificationCode, userRegisterData.email()));

        // then
        StepVerifier.create(activateUserAccount)
                .expectError(ActivationCodeNotFoundException.class)
                .verify();

        StepVerifier.create(isActivationCodePresentForUser(userRegisterData.email()))
                .verifyComplete();

        Mockito.verify(userAuthPort, Mockito.times(0)).activateUserAccount(userRegisterData.email());
    }

    @Test
    public void whenAuthServiceReturnErrorActiveAccountShouldThrowsExceptionAuthServiceException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.error(new AuthServiceException("Save verification code error")));

        String verificationCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);

        userPort.registerUser(userRegisterData).block();

        // when
        Mono<Void> activateUserAccount = userPort.activateUserAccount(new ActiveAccountCodeData(verificationCode, userRegisterData.email()));

        // then
        StepVerifier.create(activateUserAccount)
                .expectError(AuthServiceException.class)
                .verify();

    }
}