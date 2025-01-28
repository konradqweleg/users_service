package com.example.usersservices_mychatserver.integration.unit.core;


import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTests {

    @MockBean
    protected UserAuthPort userAuthPort;

    @Autowired
    protected UserPort userPort;

    @MockBean
    protected GenerateRandomCodePort generateRandomCodePort;


    protected void fullRegisterAndActivateUserAccount(UserRegisterDataDTO userRegisterData) {
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.empty());
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(true));

        String verificationCode = "123456";
        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);

        userPort.registerUser(userRegisterData).block();

        Mono<Void> activeAccount = userPort.activateUserAccount(new ActiveAccountCodeData(verificationCode, userRegisterData.email()));

        StepVerifier.create(activeAccount)
                .expectComplete()
                .verify();
    }

    protected void registerUserWithoutActivateAccountWithSpecificActiveAccountCode(UserRegisterDataDTO userRegisterData, String verificationCode) {
        when(userAuthPort.register(userRegisterData)).thenReturn(Mono.empty());
        when(userAuthPort.activateUserAccount(userRegisterData.email())).thenReturn(Mono.empty());
        when(userAuthPort.isEmailAlreadyActivatedUserAccount(userRegisterData.email())).thenReturn(Mono.just(false));

        userPort.registerUser(userRegisterData).block();
    }



}
