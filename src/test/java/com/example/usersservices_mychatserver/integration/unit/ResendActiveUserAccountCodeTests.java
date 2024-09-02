package com.example.usersservices_mychatserver.integration.unit;

import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
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
public class ResendActiveUserAccountCodeTests {
    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    HashPasswordPort hashPasswordPort;

    @MockBean
    GenerateRandomCodePort generateCode;

    @Autowired
    UserPort userPort;

    @MockBean
    SendEmailToUserPort sendEmailPort;



    @Test
    void ifUserNotExistsRequestShouldFail() {

        //given
        when(userRepositoryPort.findUserWithEmail("mail@mail.pl")).thenReturn(Mono.empty());

        //when
        UserEmailData userLoginData = new UserEmailData("noExistsUser");
        Mono<Result<Status>> resendEmailNoExistsUser = userPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(resendEmailNoExistsUser)
                .expectNextMatches(response -> response.isError() && response.getError().equals(ErrorMessage.USER_NOT_FOUND.getFullJSON()))
                .expectComplete()
                .verify();

    }

    @Test
    void ifUserAlreadyActiveRequestShouldFail() {
        //given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.just(new UserMyChat(1L, "root", "surname", "mail@mail.pl", "password", 1, true)));

        //when
        UserEmailData userLoginData = new UserEmailData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = userPort.resendActiveUserAccountCode(Mono.just(userLoginData));

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
        UserEmailData userLoginData = new UserEmailData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = userPort.resendActiveUserAccountCode(Mono.just(userLoginData));

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
        when(userRepositoryPort.deleteUserActiveAccountCode(1L)).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

        //when
        UserEmailData userLoginData = new UserEmailData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = userPort.resendActiveUserAccountCode(Mono.just(userLoginData));

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
        when(userRepositoryPort.deleteUserActiveAccountCode(1L)).thenReturn(Mono.empty());
        when(userRepositoryPort.saveVerificationCode(any())).thenReturn(Mono.error(new RuntimeException(ErrorMessage.RESPONSE_NOT_AVAILABLE.getMessage())));

        //when
        UserEmailData userLoginData = new UserEmailData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = userPort.resendActiveUserAccountCode(Mono.just(userLoginData));

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
        when(userRepositoryPort.deleteUserActiveAccountCode(1L)).thenReturn(Mono.empty());
        when(userRepositoryPort.saveVerificationCode(any())).thenReturn(Mono.empty());

        //when
        UserEmailData userLoginData = new UserEmailData("mail@mail.pl");
        Mono<Result<Status>> alreadyActivatedUser = userPort.resendActiveUserAccountCode(Mono.just(userLoginData));

        //then
        StepVerifier
                .create(alreadyActivatedUser)
                .expectNextMatches(result -> result.isSuccess() && result.getValue().correctResponse())
                .expectComplete()
                .verify();

    }


}
