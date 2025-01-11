package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterUserTests {

    private static final String EMAIL = "mail@mail.pl";
    private static final String VERIFICATION_CODE = "123456";
    private static final UserRegisterDataDTO USER_REGISTER_DATA = new UserRegisterDataDTO("root", "surname", EMAIL, "password");

    @MockBean
    private UserAuthPort userAuthPort;

    @Autowired
    private UserPort userPort;

    @MockBean
    private SendEmailToUserPort sendEmailPort;

    @Autowired
    private DatabaseClient databaseClient;

    @MockBean
    private GenerateRandomCodePort generateRandomCodePort;

    @BeforeEach
    public void setup() {
        truncateTables().block();
        when(userAuthPort.register(USER_REGISTER_DATA)).thenReturn(Mono.empty());
        when(generateRandomCodePort.generateCode()).thenReturn(VERIFICATION_CODE);
    }

    private Mono<Void> truncateTables() {
        return databaseClient.sql("TRUNCATE TABLE USER_MY_CHAT").then();
    }

    @Test
    public void whenUserAuthServiceReturnErrorRegisterUserActionShouldAlsoFail() {
        // given
        when(userAuthPort.register(USER_REGISTER_DATA)).thenReturn(Mono.error(new AuthServiceException("Auth service error")));

        // when
        Mono<Void> registerUserResult = userPort.registerUser(USER_REGISTER_DATA);

        // then
        StepVerifier
                .create(registerUserResult)
                .expectError(AuthServiceException.class)
                .verify();
    }

    @Test
    public void whenCorrectRegisterDataUserShouldBeSavedInDb() {
        // when
        Mono<Void> registerUserResult = userPort.registerUser(USER_REGISTER_DATA);

        // then
        StepVerifier
                .create(registerUserResult)
                .expectComplete()
                .verify();

        verifyUserSavedInDb(EMAIL);
    }

    private void verifyUserSavedInDb(String email) {
        String sqlSelectUser = "SELECT * FROM USER_MY_CHAT WHERE email = '" + email + "'";
        Flux<UserMyChat> userFlux = databaseClient.sql(sqlSelectUser)
                .map((row, metadata) -> new UserMyChat(
                        Objects.requireNonNull(row.get("id", Integer.class)).longValue(),
                        row.get("name", String.class),
                        row.get("surname", String.class),
                        row.get("email", String.class)
                ))
                .all();

        StepVerifier.create(userFlux)
                .expectNextMatches(user -> user.email().equals(email))
                .expectComplete()
                .verify();
    }

    @Test
    public void whenCorrectRegisterDataEmailWithActivationCodeShouldBeSent() {
        // when
        Mono<Void> registerUserResult = userPort.registerUser(USER_REGISTER_DATA);

        // then
        StepVerifier
                .create(registerUserResult)
                .expectComplete()
                .verify();

        Mockito.verify(sendEmailPort, Mockito.times(1)).sendVerificationCode(EMAIL, VERIFICATION_CODE);
    }
}