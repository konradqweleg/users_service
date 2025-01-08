package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.LoginDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.exception.auth.UnauthorizedException;
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
    public void whenCorrectLoginDataMethodShouldReturnAuthTokens() {

        //given
        LoginDataDTO correctLoginDataDTO = new LoginDataDTO("mail@mail.pl", "password");
        when(userAuthPort.isEmailAlreadyRegistered(correctLoginDataDTO.email())).thenReturn(Mono.just(true));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(correctLoginDataDTO.email())).thenReturn(Mono.just(true));

        UserAccessData userTokens = new UserAccessData("accessToken", "refreshToken", "sessionState");
        when(userAuthPort.authorizeUser(correctLoginDataDTO)).thenReturn(Mono.just(userTokens));

        //when
        Mono<UserAccessData> loginResponse = userPort.login(correctLoginDataDTO);

        //then
        StepVerifier
                .create(loginResponse)
                .expectNextMatches(result -> result.equals(userTokens))
                .expectComplete()
                .verify();
    }


    @Test
    public void whenWrongPasswordMethodShouldReturnUnauthorized() {
        //given
        LoginDataDTO loginDataDTOWithBadPassword = new LoginDataDTO("correct@mail.pl","wrongPassword");
        when(userAuthPort.isEmailAlreadyRegistered(loginDataDTOWithBadPassword.email())).thenReturn(Mono.just(true));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(loginDataDTOWithBadPassword.email())).thenReturn(Mono.just(true));

        when(userAuthPort.authorizeUser(loginDataDTOWithBadPassword)).thenThrow(new UnauthorizedException("User unauthorized."));

        //when
        Mono<UserAccessData> loginResponse = userPort.login(loginDataDTOWithBadPassword);

        //then
        StepVerifier
                .create(loginResponse)
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("User unauthorized."))
                .verify();
    }

    @Test
    public void whenUserWithEmailNoExistsMethodsShouldReturnUnauthorized() {
        //given
        LoginDataDTO loginDataDTOWithNoExistsUser = new LoginDataDTO("noexists@mail.pl","password");
        when(userAuthPort.isEmailAlreadyRegistered(loginDataDTOWithNoExistsUser.email())).thenReturn(Mono.just(false));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(loginDataDTOWithNoExistsUser.email())).thenReturn(Mono.just(false));

        when(userAuthPort.authorizeUser(loginDataDTOWithNoExistsUser)).thenThrow(new UnauthorizedException("User unauthorized."));

        //when
        Mono<UserAccessData> loginResponse = userPort.login(loginDataDTOWithNoExistsUser);

        //then
        StepVerifier
                .create(loginResponse)
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("User not found"))
                .verify();
    }

    @Test
    public void whenAuthorizeUserThrowsAuthServiceExceptionShouldReturnUnauthorized() {
        //given
        LoginDataDTO loginDataDTO = new LoginDataDTO("correct@mail.pl", "password");
        when(userAuthPort.isEmailAlreadyRegistered(loginDataDTO.email())).thenReturn(Mono.just(true));
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(loginDataDTO.email())).thenReturn(Mono.just(true));

        when(userAuthPort.authorizeUser(loginDataDTO)).thenReturn(Mono.error(new AuthServiceException("Authorization failed")));

        //when
        Mono<UserAccessData> loginResponse = userPort.login(loginDataDTO);

        //then
        StepVerifier
                .create(loginResponse)
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("User unauthorized"))
                .verify();
    }

}
