package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.config.keycloak.KeyCloakConfiguration;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterUserTestsMyChat {

//    @MockBean
//    private UserRepositoryPort userRepositoryPort;
//
//    @MockBean
//    GenerateRandomCodePort generateCode;
//
//    @MockBean
//    UserAuthPort userAuthPort;
//
//    @Autowired
//    UserPort userPort;
//
//    @MockBean
//    SendEmailToUserPort sendEmailPort;
//
//    @MockBean
//    private Keycloak keycloak;
//
//    @MockBean
//    private KeyCloakConfiguration keyCloakConfiguration;
//
//    @Mock
//    private GenerateRandomCodePort generateRandomCodePort;
//    @Test
//    public void testRegisterUser_AuthServiceFailure() {
//
//        // given
//        Mono<UserRegisterDataDTO> userRegisterData = Mono.just(new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password"));
//        when(userAuthPort.register(any())).thenReturn(Mono.just(new Status(false)));
//
//        // when
//        Mono<Result<Status>> registerAlreadyExistsUserResult = userPort.registerUser(userRegisterData);
//
//        // then
//        StepVerifier
//                .create(registerAlreadyExistsUserResult)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testRegisterUser_Success() {
//        // given
//        UserRegisterDataDTO userRegisterDataDTO = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
//        UserMyChat savedUserMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
//        String verificationCode = "123456";
//
//        when(userAuthPort.register(any())).thenReturn(Mono.just(new Status(true)));
//        when(userRepositoryPort.saveUser(any())).thenReturn(Mono.just(savedUserMyChat));
//        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);
//        when(userRepositoryPort.saveVerificationCode(any())).thenReturn(Mono.just(new CodeVerification(null, savedUserMyChat.id(), verificationCode)));
//
//        // when
//        Mono<Result<Status>> result = userPort.registerUser(Mono.just(userRegisterDataDTO));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isSuccess)
//                .expectComplete()
//                .verify();
//        Mockito.verify(sendEmailPort, Mockito.times(1)).sendVerificationCode(any(), any());
//    }
//
//    @Test
//    public void testRegisterUser_SaveUserFailure() {
//        // given
//        UserRegisterDataDTO userRegisterDataDTO = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
//
//        when(userAuthPort.register(any())).thenReturn(Mono.just(new Status(true)));
//        when(userRepositoryPort.saveUser(any())).thenReturn(Mono.error(new RuntimeException("Save user error")));
//
//        // when
//        Mono<Result<Status>> result = userPort.registerUser(Mono.just(userRegisterDataDTO));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//
//    @Test
//    public void testRegisterUser_SaveVerificationCodeFailure() {
//        // given
//        UserRegisterDataDTO userRegisterDataDTO = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
//        UserMyChat savedUserMyChat = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
//        String verificationCode = "123456";
//
//        when(userAuthPort.register(any())).thenReturn(Mono.just(new Status(true)));
//        when(userRepositoryPort.saveUser(any())).thenReturn(Mono.just(savedUserMyChat));
//        when(generateRandomCodePort.generateCode()).thenReturn(verificationCode);
//        when(userRepositoryPort.saveVerificationCode(any())).thenReturn(Mono.error(new RuntimeException("Save verification code error")));
//
//        // when
//        Mono<Result<Status>> result = userPort.registerUser(Mono.just(userRegisterDataDTO));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }



}
