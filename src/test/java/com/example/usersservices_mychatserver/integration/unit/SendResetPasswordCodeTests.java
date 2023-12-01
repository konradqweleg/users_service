package com.example.usersservices_mychatserver.integration.unit;

import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.service.message.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendResetPasswordCodeTests {
    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    HashPasswordPort hashPasswordPort;

    @MockBean
    GenerateRandomCodePort generateCode;



    @MockBean
    SendEmailToUserPort sendEmailPort;

    @Autowired
    private UserPort userPort;


    @Test void ifUserNotExistsRequestShouldFail() {

        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.empty());

        //when
        Mono<Result<Status>> resendEmailNoExistsUser = userPort.sendResetPasswordCode(Mono.just(new UserEmailData("email@email.pl")));

        //then
        StepVerifier
                .create(resendEmailNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }

    @Test void ifDatabaseNotAvailableForFindUserWithEmailRequestShouldFail() {
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
        //when
        Mono<Result<Status>> resendEmailNoExistsUser = userPort.sendResetPasswordCode(Mono.just(new UserEmailData("email@email.pl")));

        //then
        StepVerifier
                .create(resendEmailNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }

    @Test void ifDatabaseNotAvailableForDeleteResetPasswordCodeForUserRequestShouldFail() {
        //given
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
        //when
        Mono<Result<Status>> resendEmailNoExistsUser = userPort.sendResetPasswordCode(Mono.just(new UserEmailData("email@email.pl")));

        //then
        StepVerifier
                .create(resendEmailNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }

    @Test void ifDatabaseNotAvailableForInsertResetPasswordCodeRequestShouldFail() {
        //given
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
        when(userRepositoryPort.insertResetPasswordCode(any())).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));
        //when
        Mono<Result<Status>> resendEmailNoExistsUser = userPort.sendResetPasswordCode(Mono.just(new UserEmailData("email@email.pl")));

        //then
        StepVerifier
                .create(resendEmailNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }

    @Test void ifCorrectRequestDataRequestShouldSuccess() {
        //given
        when(userRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
        when(userRepositoryPort.insertResetPasswordCode(any())).thenReturn(Mono.empty());
        //when
        Mono<Result<Status>> resendEmailNoExistsUser = userPort.sendResetPasswordCode(Mono.just(new UserEmailData("email@email.pl")));

        //then
        StepVerifier
                .create(resendEmailNoExistsUser)
                .expectNextMatches(Result::isSuccess)
                .expectComplete()
                .verify();

    }



}
