package com.example.usersservices_mychatserver.integration.unit.core;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTests {

//    @MockBean
//    private UserRepositoryPort userRepositoryPort;
//
//
//
//    @MockBean
//    HashPasswordPort hashPasswordPort;
//
//    @Autowired
//    private UserPort userPort;
//
//    @Test
//    public void ifCorrectCredentialsShouldReturnTrue() {
//
//        //given
//        EmailAndPasswordData correctLoginData = new EmailAndPasswordData("mail@mail.pl", "password");
//        when(userRepositoryPort.findUserWithEmail(correctLoginData.email())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
//        when(hashPasswordPort.checkPassword(correctLoginData.password(), "password")).thenReturn(true);
//        //when
//        Mono<Result<IsCorrectCredentials>> isCorrectCredentialsResult = userPort.isCorrectLoginCredentials(Mono.just(correctLoginData));
//
//        //then
//        StepVerifier
//                .create(isCorrectCredentialsResult)
//                .expectNextMatches(result -> result.isSuccess() && result.getValue().isCorrectCredentials())
//                .expectComplete()
//                .verify();
//    }
//
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
