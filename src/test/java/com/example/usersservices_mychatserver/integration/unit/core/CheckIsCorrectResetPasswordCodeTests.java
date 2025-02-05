package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeDTO;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.exception.password_reset.UserToResetPasswordDoesNotExistsException;
import com.example.usersservices_mychatserver.port.in.UserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

public class CheckIsCorrectResetPasswordCodeTests extends BaseTests {


    @Autowired
    private UserPort userPort;

    @Autowired
    private DatabaseClient databaseClient;

    private static final String USER_RESET_PASSWORD_CODE = "123456";


    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }


    @Test
    public void whenCorrectCodeShouldReturnTrue() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.com", "password");

        fullRegisterAndActivateUserAccount(userRegisterData);

        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        when(generateRandomCodePort.generateCode()).thenReturn(USER_RESET_PASSWORD_CODE);

        Mono<Void> resultSendResetPasswordCode = userPort.sendResetPasswordCode(emailData);

        StepVerifier.create(resultSendResetPasswordCode)
                .expectComplete()
                .verify();


        // when
        UserEmailAndCodeDTO emailAndCodeData = new UserEmailAndCodeDTO(userRegisterData.email(), "123456");
        Mono<IsCorrectResetPasswordCode> result = userPort.checkIsCorrectResetPasswordCode(emailAndCodeData);

        // then
        StepVerifier.create(result)
                .expectNext(new IsCorrectResetPasswordCode(true))
                .verifyComplete();
    }

    @Test
    public void whenIncorrectCodeShouldReturnFalse() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.com", "password");

        fullRegisterAndActivateUserAccount(userRegisterData);

        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        when(generateRandomCodePort.generateCode()).thenReturn(USER_RESET_PASSWORD_CODE);

        Mono<Void> resultSendResetPasswordCode = userPort.sendResetPasswordCode(emailData);

        StepVerifier.create(resultSendResetPasswordCode)
                .expectComplete()
                .verify();


        // when
        String badCode = "654321";
        UserEmailAndCodeDTO emailAndCodeData = new UserEmailAndCodeDTO(userRegisterData.email(), badCode);
        Mono<IsCorrectResetPasswordCode> result = userPort.checkIsCorrectResetPasswordCode(emailAndCodeData);

        // then
        StepVerifier.create(result)
                .expectNext(new IsCorrectResetPasswordCode(false))
                .verifyComplete();
    }

    @Test
    public void whenUserNotFoundShouldReturnException() {
        // given
        UserEmailAndCodeDTO emailAndCodeData = new UserEmailAndCodeDTO("mail@mail.pl", "123456");

        // when
        Mono<IsCorrectResetPasswordCode> result = userPort.checkIsCorrectResetPasswordCode(emailAndCodeData);

        // then
        StepVerifier.create(result)
                .expectError(UserToResetPasswordDoesNotExistsException.class)
                .verify();
    }
}
