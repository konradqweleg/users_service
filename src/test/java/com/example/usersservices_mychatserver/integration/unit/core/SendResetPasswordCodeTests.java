package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.password_reset.UserAccountIsNotActivatedException;
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


//    @MockBean
//    private UserRepositoryPort userRepositoryPort;
//
//    @MockBean
//    GenerateRandomCodePort generateCode;
//
//    @MockBean
//    UserAuthPort userAuthPort;
//
//    @Autowired
//    UserPort userPort;
//
//    @MockBean
//    SendEmailToUserPort sendEmailPort;
//
//    @MockBean
//    private Keycloak keycloak;
//
//    @MockBean
//    private KeyCloakConfiguration keyCloakConfiguration;
//
//    @Mock
//    private GenerateRandomCodePort generateRandomCodePort;
//
//
    private static final String SQL_TRUNCATE_USER_TABLE = "TRUNCATE TABLE USER_MY_CHAT";
    private static final String SQL_TRUNCATE_CODE_VERIFICATION_TABLE = "TRUNCATE TABLE code_verification";
    @BeforeEach
    public void setup() {
        truncateTables().block();
    }

    private Mono<Void> truncateTables() {
        return databaseClient.sql(SQL_TRUNCATE_USER_TABLE).then()
                .then(databaseClient.sql(SQL_TRUNCATE_CODE_VERIFICATION_TABLE).then());
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

//    private void registerAndActivateUser(UserRegisterDataDTO userRegisterData, String verificationCode) {
//        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
//        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.empty());
//        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(true));
//
//        userPort.registerUser(userRegisterData).block();
//
//        Mono<Void> activeAccount = userPort.activateUserAccount(new ActiveAccountCodeData(verificationCode, userRegisterData.email()));
//
//        StepVerifier.create(activeAccount)
//                .expectComplete()
//                .verify();
//    }

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
//
//    @Test
//    public void testSendResetPasswordCode_UserNotFound() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.empty());
//
//        // when
//        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testSendResetPasswordCode_ErrorDuringProcess() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.error(new RuntimeException("Unexpected error")));
//
//        // when
//        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testSendResetPasswordCode_DeleteResetPasswordCodeFailure() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
//        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(true));
//        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.error(new RuntimeException("Delete reset password code error")));
//
//        // when
//        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testSendResetPasswordCode_InsertResetPasswordCodeFailure() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
//
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChat));
//        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(true));
//        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
//        when(userRepositoryPort.insertResetPasswordCode(any())).thenReturn(Mono.error(new RuntimeException("Insert reset password code error")));
//
//        // when
//        Mono<Result<Status>> result = userPort.sendResetPasswordCode(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//


}
