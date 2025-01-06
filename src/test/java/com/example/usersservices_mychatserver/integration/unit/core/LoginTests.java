package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.LoginData;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTests {

    @MockBean
    private UserAuthPort userAuthPort;

    @Autowired
    private UserPort userPort;

    @Test
    public void ifCorrectLoginDataRequestShouldReturnAuthTokens() {

        //given
        LoginData correctLoginData = new LoginData("mail@mail.pl", "password");
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(correctLoginData.email())).thenReturn(Mono.just(true));

        UserAccessData userTokens = new UserAccessData("accessToken", "refreshToken", "sessionState");
        when(userAuthPort.authorizeUser(correctLoginData)).thenReturn(Mono.just(userTokens));

        //when
        Mono<UserAccessData> loginResponse = userPort.login(correctLoginData);

        //then
        StepVerifier
                .create(loginResponse)
                .expectNextMatches(result -> result.equals(userTokens))
                .expectComplete()
                .verify();
    }

//
//    @Test
//    public void ifWrongCredentialsShouldReturnFalse() {
//
//        //given
//        EmailAndPasswordData correctLoginData = new EmailAndPasswordData("mail@mail.pl", "WrongPassword");
//        when(userRepositoryPort.findUserWithEmail(correctLoginData.email())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
//        when(hashPasswordPort.checkPassword(correctLoginData.password(), "WrongPassword")).thenReturn(false);
//        //when
//        Mono<Result<IsCorrectCredentials>> isCorrectCredentialsResult = userPort.isCorrectLoginCredentials(Mono.just(correctLoginData));
//
//        //then
//        StepVerifier
//                .create(isCorrectCredentialsResult)
//                .expectNextMatches(result -> result.isSuccess() && !result.getValue().isCorrectCredentials())
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void ifUserDoesNotExistShouldReturnFalse() {
//        //given
//        EmailAndPasswordData nonExistingUserData = new EmailAndPasswordData("nonexistent@mail.com", "password");
//        when(userRepositoryPort.findUserWithEmail(nonExistingUserData.email())).thenReturn(Mono.empty());
//
//        //when
//        Mono<Result<IsCorrectCredentials>> isCorrectCredentialsResult = userPort.isCorrectLoginCredentials(Mono.just(nonExistingUserData));
//
//        //then
//        StepVerifier
//                .create(isCorrectCredentialsResult)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//
//    @Test
//    public void ifRepositoryThrowsExceptionShouldReturnFailureResult() {
//        //given
//        EmailAndPasswordData userData = new EmailAndPasswordData("existing@mail.com", "password");
//        when(userRepositoryPort.findUserWithEmail(anyString())).thenThrow(new RuntimeException("Repository exception"));
//
//        //when
//        Mono<Result<IsCorrectCredentials>> isCorrectCredentialsResult = userPort.isCorrectLoginCredentials(Mono.just(userData));
//
//        //then
//        StepVerifier
//                .create(isCorrectCredentialsResult)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }





}
