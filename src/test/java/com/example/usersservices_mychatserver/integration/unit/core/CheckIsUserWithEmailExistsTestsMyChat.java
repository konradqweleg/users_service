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
public class CheckIsUserWithEmailExistsTestsMyChat {

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
//    public void testCheckIsUserWithThisEmailExist_Success() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//        UserMyChat userMyChatFromDb = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChatFromDb));
//        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(true));
//
//        // when
//        Mono<Result<Status>> result = userPort.checkIsUserWithThisEmailExist(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isSuccess)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testCheckIsUserWithThisEmailExist_UserNotActivated() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//        UserMyChat userMyChatFromDb = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChatFromDb));
//        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.just(false));
//
//        // when
//        Mono<Result<Status>> result = userPort.checkIsUserWithThisEmailExist(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testCheckIsUserWithThisEmailExist_UserNotFound() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.empty());
//
//        // when
//        Mono<Result<Status>> result = userPort.checkIsUserWithThisEmailExist(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testCheckIsUserWithThisEmailExist_ErrorDuringProcess() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.error(new RuntimeException("Unexpected error")));
//
//        // when
//        Mono<Result<Status>> result = userPort.checkIsUserWithThisEmailExist(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    public void testCheckIsUserWithThisEmailExist_IsActivatedUserAccountFailure() {
//        // given
//        UserEmailData emailData = new UserEmailData("mail@mail.pl");
//        UserMyChat userMyChatFromDb = new UserMyChat(1L, "root", "surname", "mail@mail.pl");
//
//        when(userRepositoryPort.findUserWithEmail(emailData.email())).thenReturn(Mono.just(userMyChatFromDb));
//        when(userAuthPort.isEmailAlreadyActivatedUserAccount(any())).thenReturn(Mono.error(new RuntimeException("Activation check error")));
//
//        // when
//        Mono<Result<Status>> result = userPort.checkIsUserWithThisEmailExist(Mono.just(emailData));
//
//        // then
//        StepVerifier.create(result)
//                .expectNextMatches(Result::isError)
//                .expectComplete()
//                .verify();
//    }
}
