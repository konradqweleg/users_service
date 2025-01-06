package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.config.keycloak.KeyCloakConfiguration;
import com.example.usersservices_mychatserver.entity.request.LoginData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.mockito.Mock;
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
public class AuthorizeUserTestsMyChat {

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
//    public void testAuthorizeUser_Success() {
//        // given
//        LoginData loginData = new LoginData("username", "password");
//        UserAccessData userAccessData = new UserAccessData("token", "refreshToken","sessionState");
//
//        when(userAuthPort.authorizeUser(any())).thenReturn(Mono.just(userAccessData));
//
//        // when
//        Mono<Result<UserAccessData>> result = userPort.login(Mono.just(loginData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isSuccess)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testAuthorizeUser_Failure() {
//        // given
//        LoginData loginData = new LoginData("username", "password");
//
//        when(userAuthPort.authorizeUser(any())).thenReturn(Mono.error(new RuntimeException("Authorization error")));
//
//        // when
//        Mono<Result<UserAccessData>> result = userPort.login(Mono.just(loginData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
}
