package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.activation.UserAlreadyActivatedException;
import com.example.usersservices_mychatserver.exception.activation.UserToResendActiveAccountCodeNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

public class ResendActiveUserAccountCodeTests extends BaseTests{


    private static final String SQL_GET_USER_ID = "SELECT id FROM user_my_chat WHERE email = :email";

    private static final String SQL_CHECK_IF_CODE_MATCH_TO_EXPECTED = "SELECT COUNT(*) AS count FROM code_verification WHERE id_user = :userId AND code = :expectedCode";

    @Autowired
    private DatabaseClient databaseClient;


    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }



    private Mono<Boolean> isActivationCodeMatchingForUser(String email, String expectedCode) {
        return databaseClient.sql(SQL_GET_USER_ID)
                .bind("email", email)
                .map((row, metadata) -> row.get("id", Integer.class))
                .one()
                .flatMap(userId -> databaseClient.sql(SQL_CHECK_IF_CODE_MATCH_TO_EXPECTED)
                        .bind("userId", userId)
                        .bind("expectedCode", expectedCode)
                        .map((row, metadata) -> row.get("count", Long.class) > 0)
                        .one());
    }


    @Test
    public void whenNoActivatedUserAndCorrectUserEmailActiveAccountCodeShouldBeResend() {
        //given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");

        String ActiveAccountCodeSendAfterRegister = "123456";
        String ActiveAccountCodeSendAfterResendOperation = "654321";
        when(generateRandomCodePort.generateCode()).thenReturn(ActiveAccountCodeSendAfterRegister, ActiveAccountCodeSendAfterResendOperation);


        registerUserWithoutActivateAccountWithSpecificActiveAccountCode(userRegisterData, ActiveAccountCodeSendAfterRegister);

        StepVerifier.create(isActivationCodeMatchingForUser(userRegisterData.email(), ActiveAccountCodeSendAfterRegister))
                .expectNext(true)
                .verifyComplete();

        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(false));


        //when
        Mono<Void> resendActiveAccountCode = userPort.resendActiveUserAccountCode(new UserEmailDataDTO(userRegisterData.email()));

        //then
        StepVerifier.create(resendActiveAccountCode)
                .expectComplete()
                .verify();

        StepVerifier.create(isActivationCodeMatchingForUser(userRegisterData.email(), ActiveAccountCodeSendAfterResendOperation))
                .expectNext(true)
                .verifyComplete();


    }

    @Test
    public void whenUserNotFoundForResendActivationCodeShouldThrowException() {
        // given
        // when
        String notExistingEmail = "noexist@mail.pl";
        Mono<Void> resendActiveAccountCode = userPort.resendActiveUserAccountCode(new UserEmailDataDTO(notExistingEmail));

        // then
        StepVerifier.create(resendActiveAccountCode)
                .expectError(UserToResendActiveAccountCodeNotExistsException.class)
                .verify();
    }

    @Test
    public void whenUserAlreadyActivatedShouldThrowException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);

        // when
        Mono<Void> resendActiveAccountCode = userPort.resendActiveUserAccountCode(new UserEmailDataDTO(userRegisterData.email()));

        // then
        StepVerifier.create(resendActiveAccountCode)
                .expectError(UserAlreadyActivatedException.class)
                .verify();
    }


}
