package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterUserTestMockDb {

    private static final String EMAIL = "mail@mail.pl";
    private static final String VERIFICATION_CODE = "123456";
    private static final UserRegisterDataDTO USER_REGISTER_DATA = new UserRegisterDataDTO("root", "surname", EMAIL, "password");

    @MockBean
    private UserAuthPort userAuthPort;

    @Autowired
    private UserPort userPort;

    @MockBean
    private GenerateRandomCodePort generateRandomCodePort;

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @BeforeEach
    public void setup() {
        when(userAuthPort.register(USER_REGISTER_DATA)).thenReturn(Mono.empty());
        when(generateRandomCodePort.generateCode()).thenReturn(VERIFICATION_CODE);
    }

    @Test
    public void whenUserSaveInDbThrowsErrorMethodShouldReturnExceptionSaveInRepositoryException() {
        // given
        when(userRepositoryPort.saveUser(any())).thenReturn(Mono.error(new Exception("Save user error")));

        // when
        Mono<Void> registerUserResult = userPort.registerUser(USER_REGISTER_DATA);

        // then
        StepVerifier
                .create(registerUserResult)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }

    @Test
    public void whenSaveVerificationCodeInDbThrowsExceptionMethodShouldReturnErrorSaveInRepositoryException() {
        // given
        when(userRepositoryPort.saveVerificationCode(any())).thenReturn(Mono.error(new Exception("Save verification code error")));
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", EMAIL);
        when(userRepositoryPort.saveUser(any())).thenReturn(Mono.just(userMyChat));

        // when
        Mono<Void> registerUserResult = userPort.registerUser(USER_REGISTER_DATA);

        // then
        StepVerifier
                .create(registerUserResult)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }
}