package com.example.usersservices_mychatserver.integration.unit;

import com.example.usersservices_mychatserver.entity.request.UserLoginData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.ActiveUserAccountPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
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
public class ResendActiveUserAccountCode {
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
    private ActiveUserAccountPort activeUserAccountPort;

    @Test
    void ifUserNotExistsRequestShouldFail() {

        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.empty());

        //when
        UserLoginData userLoginData = new UserLoginData("noExistsUser");
        Mono<Result<Status>> resendEmailNoExistsUser = activeUserAccountPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(resendEmailNoExistsUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }

    @Test
    void ifUserAlreadyActiveRequestShouldFail() {
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));

        //when
        UserLoginData userLoginData = new UserLoginData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = activeUserAccountPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(alreadyActivatedUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();
    }

    @Test
    void ifDatabaseNotAvailableForFindUserWithEmailRequestShouldFail() {
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

        //when
        UserLoginData userLoginData = new UserLoginData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = activeUserAccountPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(alreadyActivatedUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }


    @Test
    void ifDatabaseNotAvailableForDeleteUserActivationCodeRequestShouldFail() {
        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, false)));
        when(generateCode.generateCode()).thenReturn("000000");
        when(codeVerificationRepository.deleteUserActivationCode(1L)).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

        //when
        UserLoginData userLoginData = new UserLoginData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = activeUserAccountPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(alreadyActivatedUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }


    @Test
    void ifDatabaseNotAvailableForSaveVerificationCodeRequestShouldFail() {
        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, false)));
        when(generateCode.generateCode()).thenReturn("000000");
        when(codeVerificationRepository.deleteUserActivationCode(1L)).thenReturn(Mono.empty());
        when(codeVerificationRepository.saveVerificationCode(any())).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

        //when
        UserLoginData userLoginData = new UserLoginData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = activeUserAccountPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(alreadyActivatedUser)
                .expectNextMatches(Result::isError)
                .expectComplete()
                .verify();

    }


    @Test void ifCorrectRequestDataUserActiveAccountCodeShouldResendAndOldDelete() {
        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, false)));
        when(generateCode.generateCode()).thenReturn("000000");
        when(codeVerificationRepository.deleteUserActivationCode(1L)).thenReturn(Mono.empty());
        when(codeVerificationRepository.saveVerificationCode(any())).thenReturn(Mono.empty());

        //when
        UserLoginData userLoginData = new UserLoginData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = activeUserAccountPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(alreadyActivatedUser)
                .expectNextMatches(result -> result.isSuccess() && result.getValue().correctResponse())
                .expectComplete()
                .verify();

    }


}
