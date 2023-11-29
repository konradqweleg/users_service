package com.example.usersservices_mychatserver.integration.unit;


import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.AuthenticationUserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResetPasswordTest {
    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    HashPasswordPort hashPasswordPort;

    @MockBean
    GenerateRandomCodePort generateCode;

    @MockBean
    CodeVerificationRepositoryPort codeVerificationRepository;

    @MockBean
    ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort;

    @MockBean
    SendEmailToUserPort sendEmailPort;

    @Autowired
    private AuthenticationUserPort authenticationUserPort;



    @Test
    void ifUserNotExistsInFindUserWithEmailRequestShouldFail() {
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.empty());
        //when
        Mono<Result<Status>> changePasswordNoExistsUser = authenticationUserPort.changePassword(Mono.just(new ChangePasswordData("email@noexists", "code", "password")));
        //then
        StepVerifier
                .create(changePasswordNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }

    @Test
    void whenChangePasswordCodeNoExistsForThisUserRequestShouldFail(){
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "user", "password", "email@email.eu", "password",1,true)));
        when(resetPasswordCodeRepositoryPort.findResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
        //when
        Mono<Result<Status>> changePasswordNoExistsUser = authenticationUserPort.changePassword(Mono.just(new ChangePasswordData("email@email.eu", "code", "password")));
        //then
        StepVerifier
                .create(changePasswordNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }


    @Test
    void whenChangePasswordCodeIsBadForThisUserRequestShouldFail(){
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "user", "password", "email@email.eu", "password",1,true)));
        when(resetPasswordCodeRepositoryPort.findResetPasswordCodeForUser(any())).thenReturn(Mono.just(new ResetPasswordCode(1L,1L,"0000")));
        //when
        Mono<Result<Status>> changePasswordNoExistsUser = authenticationUserPort.changePassword(Mono.just(new ChangePasswordData("email@email.eu", "1111", "password")));
        //then
        StepVerifier
                .create(changePasswordNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test void whenChangePasswordDataIsCorrectRequestShouldSuccess() {
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "user", "password", "email@email.eu", "password",1,true)));
        when(resetPasswordCodeRepositoryPort.findResetPasswordCodeForUser(any())).thenReturn(Mono.just(new ResetPasswordCode(1L,1L,"0000")));
        when(hashPasswordPort.cryptPassword(any())).thenReturn("password");
        when(userRepositoryPort.changePassword(any(),any())).thenReturn(Mono.empty());
        when(resetPasswordCodeRepositoryPort.deleteResetPasswordCodeForUser(any())).thenReturn(Mono.empty());
        //when
        Mono<Result<Status>> correctChangePassword = authenticationUserPort.changePassword(Mono.just(new ChangePasswordData("email@email.eu", "0000", "password")));
        //then
        StepVerifier
                .create(correctChangePassword)
                .expectNextMatches(Result::isSuccess)
                .expectComplete()
                .verify();

    }




}
