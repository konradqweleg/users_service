package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.activation.UserAlreadyActivatedException;
import com.example.usersservices_mychatserver.exception.activation.UserToResendActiveAccountCodeNotExistsException;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResendActiveUserAccountCodeTests {

    private static final String SQL_TRUNCATE_USER_TABLE = "TRUNCATE TABLE USER_MY_CHAT";
    private static final String SQL_TRUNCATE_CODE_VERIFICATION_TABLE = "TRUNCATE TABLE code_verification";
    private static final String SQL_GET_USER_ID = "SELECT id FROM user_my_chat WHERE email = :email";

    private static final String SQL_CHECK_IF_CODE_MATCH_TO_EXPECTED = "SELECT COUNT(*) AS count FROM code_verification WHERE id_user = :userId AND code = :expectedCode";

    @MockBean
    private UserAuthPort userAuthPort;

    @Autowired
    private UserPort userPort;

    @Autowired
    private DatabaseClient databaseClient;

    @MockBean
    private GenerateRandomCodePort generateRandomCodePort;

    @BeforeEach
    public void setup() {
        truncateTables().block();
    }

    private Mono<Void> truncateTables() {
        return databaseClient.sql(SQL_TRUNCATE_USER_TABLE).then()
                .then(databaseClient.sql(SQL_TRUNCATE_CODE_VERIFICATION_TABLE).then());
    }


    public Mono<Boolean> isActivationCodeMatchingForUser(String email, String expectedCode) {
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
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());

        String ActiveAccountCodeSendAfterRegister = "123456";
        String ActiveAccountCodeSendAfterResendOperation = "654321";
        when(generateRandomCodePort.generateCode()).thenReturn(ActiveAccountCodeSendAfterRegister, ActiveAccountCodeSendAfterResendOperation);

        userPort.registerUser(userRegisterData).block();

        StepVerifier.create(isActivationCodeMatchingForUser(userRegisterData.email(), ActiveAccountCodeSendAfterRegister))
                .expectNext(true)
                .verifyComplete();

        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(false));


        //when
        Mono<Void> resendActiveAccountCode = userPort.resendActiveUserAccountCode(new UserEmailData(userRegisterData.email()));

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
        Mono<Void> resendActiveAccountCode = userPort.resendActiveUserAccountCode(new UserEmailData(notExistingEmail));

        // then
        StepVerifier.create(resendActiveAccountCode)
                .expectError(UserToResendActiveAccountCodeNotExistsException.class)
                .verify();
    }

    @Test
    public void whenUserAlreadyActivatedShouldThrowException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());

        String activeUserAccountCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(activeUserAccountCode);

        userPort.registerUser(userRegisterData).block();

        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(true));

        // when
        Mono<Void> resendActiveAccountCode = userPort.resendActiveUserAccountCode(new UserEmailData(userRegisterData.email()));

        // then
        StepVerifier.create(resendActiveAccountCode)
                .expectError(UserAlreadyActivatedException.class)
                .verify();
    }


}
