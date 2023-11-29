package com.example.usersservices_mychatserver.integration.unit;

import com.example.usersservices_mychatserver.entity.request.EmailAndPasswordData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.AuthenticationUserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterUserTests {

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    HashPasswordPort hashPasswordPort;

    @MockBean
    GenerateRandomCodePort generateCode;

    @MockBean
    CodeVerificationRepositoryPort codeVerificationRepository;

    @MockBean
    SendEmailToUserPort sendEmailPort;

    @Autowired
    private AuthenticationUserPort authenticationUserPort;

    @Test
    public void ifUserAlreadyExistsRequestShouldFail() {

        //given
        EmailAndPasswordData emailAlreadyExistsUser = new EmailAndPasswordData("mail@mail.pl", "password");
        when(userRepositoryPort.findUserWithEmail(emailAlreadyExistsUser.email())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
        UserRegisterData userRegisterDataWithAlreadyExistsEmail = new UserRegisterData("root", "surname", emailAlreadyExistsUser.email(), "");

        //when
        Mono<Result<Status>> registerAlreadyExistsUserResult = authenticationUserPort.registerUser(Mono.just(userRegisterDataWithAlreadyExistsEmail));
        //then
        StepVerifier
                .create(registerAlreadyExistsUserResult)
                .expectNextMatches(result -> result.isError())
                .expectComplete()
                .verify();
    }


    @Test
    public void ifCorrectRegisterDataUserMailWithActiveAccountCodeShouldBeSendAndUserShouldBeCreated() {

        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.empty());
        when(userRepositoryPort.saveUser(any())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
        when(generateCode.generateCode()).thenReturn("000000");
        when(codeVerificationRepository.saveVerificationCode(any())).thenReturn(Mono.just(new CodeVerification(1L, 1L, "000000")));

        //when
        UserRegisterData correctUserRegisterData = new UserRegisterData("root", "surname", "mail@mail.pl", "");
        Mono<Result<Status>> registerUserStatus =  authenticationUserPort.registerUser(Mono.just(correctUserRegisterData));

        //then
        StepVerifier
                .create(registerUserStatus)
                .expectNextMatches(result -> result.isSuccess() && result.getValue().correctResponse())
                .expectComplete()
                .verify();
        Mockito.verify(sendEmailPort, Mockito.times(1)).sendVerificationCode(any(), any());

    }

    @Test
    public void ifDatabaseThrowsExceptionDuringFindUserWithEmailMethodShouldReturnError() {
        //given
        when(userRepositoryPort.findUserWithEmail(anyString())).thenThrow(new RuntimeException("Repository exception"));

        //when
        UserRegisterData userRegisterData = new UserRegisterData("root", "surname", "mail@mail.pl", "");
        Mono<Result<Status>> registerUserStatus =  authenticationUserPort.registerUser(Mono.just(userRegisterData));

        //then
        StepVerifier
                .create(registerUserStatus)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }


    @Test
    public void ifDatabaseThrowsExceptionDuringSaveVerificationCodeMethodShouldReturnError() {
        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.empty());
        when(userRepositoryPort.saveUser(any())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));
        when(generateCode.generateCode()).thenReturn("000000");
        when(codeVerificationRepository.saveVerificationCode(any())).thenThrow(new RuntimeException("Repository exception"));;

        //when
        UserRegisterData userRegisterData = new UserRegisterData("root", "surname", "mail@mail.pl", "");
        Mono<Result<Status>> registerUserStatus =  authenticationUserPort.registerUser(Mono.just(userRegisterData));

        //then
        StepVerifier
                .create(registerUserStatus)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    public void ifDatabaseThrowsExceptionDuringSaveUserMethodShouldReturnError() {
        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.empty());
        when(userRepositoryPort.saveUser(any())).thenThrow(new RuntimeException("Repository exception"));
        when(generateCode.generateCode()).thenReturn("000000");


        //when
        UserRegisterData userRegisterData = new UserRegisterData("root", "surname", "mail@mail.pl", "");
        Mono<Result<Status>> registerUserStatus =  authenticationUserPort.registerUser(Mono.just(userRegisterData));

        //then
        StepVerifier
                .create(registerUserStatus)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }


}
