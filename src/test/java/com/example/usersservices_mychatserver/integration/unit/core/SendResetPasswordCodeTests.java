package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.password_reset.UserAccountIsNotActivatedException;
import com.example.usersservices_mychatserver.exception.password_reset.UserToResetPasswordDoesNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;


public class SendResetPasswordCodeTests extends BaseTests {

    @Autowired
    private DatabaseClient databaseClient;




    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }

    private Mono<Boolean> isResetPasswordCodePresentForUser(String email, String expectedCode) {
        return databaseClient.sql("SELECT id FROM user_my_chat WHERE email = :email")
                .bind("email", email)
                .map((row, metadata) -> row.get("id", Integer.class))
                .one()
                .flatMap(userId -> databaseClient.sql("SELECT COUNT(*) AS count FROM reset_password_code WHERE id_user = :userId AND code = :expectedCode")
                        .bind("userId", userId)
                        .bind("expectedCode", expectedCode)
                        .map((row, metadata) -> row.get("count", Long.class) > 0)
                        .one());
    }

    private void fullRegisterAndActivateUserWithSpecificActiveAccountCode(UserRegisterDataDTO userRegisterData, String verificationCode) {
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.empty());
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(true));

        userPort.registerUser(userRegisterData).block();

        Mono<Void> activeAccount = userPort.activateUserAccount(new ActiveAccountCodeData(verificationCode, userRegisterData.email()));

        StepVerifier.create(activeAccount)
                .expectComplete()
                .verify();
    }


    @Test
    public void whenCorrectEmailShouldSendResendPasswordCode() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        String firstVerificationCode = "123456";
        String secondVerificationCode = "654321";
        when(generateRandomCodePort.generateCode()).thenReturn(firstVerificationCode, secondVerificationCode);

        fullRegisterAndActivateUserWithSpecificActiveAccountCode(userRegisterData, firstVerificationCode);

        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO("mail@mail.pl");
        Mono<Void> result = userPort.sendResetPasswordCode(emailData);

        // then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        StepVerifier.create(isResetPasswordCodePresentForUser(userRegisterData.email(), secondVerificationCode))
                .expectNext(true)
                .verifyComplete();
    }


    @Test
    public void whenUserNotActivatedShouldReturnExceptionUserAccountIsNotActivated() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        String verificationCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);

        registerUserWithoutActivateAccountWithSpecificActiveAccountCode(userRegisterData, verificationCode);

        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        Mono<Void> result = userPort.sendResetPasswordCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(UserAccountIsNotActivatedException.class)
                .verify();
    }


    @Test
    public void whenUserDoesNotExistsShouldReturnUserToResetPasswordDoesNotExistsException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        String verificationCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);


        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        Mono<Void> result = userPort.sendResetPasswordCode(emailData);

        // then
        StepVerifier.create(result)
                .expectError(UserToResetPasswordDoesNotExistsException.class)
                .verify();
    }


}
