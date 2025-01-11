package com.example.usersservices_mychatserver.integration.unit.core;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

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
