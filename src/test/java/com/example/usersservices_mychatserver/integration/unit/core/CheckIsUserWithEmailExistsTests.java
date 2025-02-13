package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckIsUserWithEmailExistsTests extends BaseTests{
    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }


    @Test
    public void whenIsEmailAlreadyActivatedUserAccountFailureShouldReturnAuthServiceException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);

        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email()))
                .thenReturn(Mono.error(new AuthServiceException("Check email activation error")));

        // when
        UserEmailDataDTO userEmailDataDTO = new UserEmailDataDTO(userRegisterData.email());
        Mono<Boolean> result = userPort.checkIsUserWithThisEmailExist(userEmailDataDTO);

        // then
        StepVerifier.create(result)
                .expectError(AuthServiceException.class)
                .verify();
    }
    @Test
    public void testUserExistsWithEmailShouldReturnTrue() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);

        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(true));

        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        Mono<Boolean> result = userPort.checkIsUserWithThisEmailExist(emailData);

        // then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

    }

    @Test
    public void testUserExistsButNotActivatedShouldReturnFalse() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);

        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(false));

        // when
        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        Mono<Boolean> result = userPort.checkIsUserWithThisEmailExist(emailData);

        // then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
    @Test
    public void testUserDoesNotExistShouldReturnFalse() {
        // when
        String nonExistentEmail = "noexists@mail.pl";
        UserEmailDataDTO emailData = new UserEmailDataDTO(nonExistentEmail);
        Mono<Boolean> result = userPort.checkIsUserWithThisEmailExist(emailData);

        // then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

}
