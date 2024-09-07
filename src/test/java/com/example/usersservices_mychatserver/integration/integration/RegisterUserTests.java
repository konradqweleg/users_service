package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.UserRegisterData;

import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.example.usersservices_mychatserver.integration.integration.responseUtil.ResponseMessageUtil;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import reactor.test.StepVerifier;

import java.net.URISyntaxException;
import java.util.List;


class RegisterUserTests extends DefaultTestConfiguration {

//
//    @MockBean
//    private SendEmailToUserPort sendEmailToUserPort;
//
//    String sqlSelectIdsAllUsers = "SELECT id FROM users_services_scheme.user_my_chat";
//
//    int expectZeroUsersInDatabase = 0;
//
//    @Test
//    public void wenAUserRegistrationRequestContainsTheEmailAddressOfAnExistingUserTheRequestShouldReturnAnError() throws URISyntaxException {
//
//        //given
//        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);
//        //when
//        webTestClient.post().uri(createRequestUtil().createRequestRegister())
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(CorrectRequestData.USER_REGISTER_DATA))
//                .exchange()
//                .expectStatus().is4xxClientError()
//                .expectBody()
//                .jsonPath("$.ErrorMessage").isEqualTo(ResponseMessageUtil.getUserAlreadyExist());
//
//        //then
//        Flux<Long> userIdDataFlux = databaseClient.sql(sqlSelectIdsAllUsers)
//                .map((row, metadata) ->
//                        row.get("id", Long.class)
//                )
//                .all();
//
//
//        int systemShouldCreateOnlyOneUser = 1;
//        StepVerifier.create(userIdDataFlux)
//                .expectNextCount(systemShouldCreateOnlyOneUser)
//                .expectComplete()
//                .verify();
//
//
//    }
//
//    @Test
//    public void whenAUserRegistrationRequestIsValidTheSystemShouldSendAnEmailWithAnAccountActivationCode() throws URISyntaxException {
//
//        //given
//        createUserAccountWithNotActiveAccount(CorrectRequestData.USER_REGISTER_DATA);
//
//        //when
//        int systemShouldSendOneEmailToUser = 1;
//        Mockito.verify(sendEmailToUserPort, Mockito.times(systemShouldSendOneEmailToUser)).sendVerificationCode(Mockito.any(), Mockito.anyString());
//
//        //then
//        String sqlSelectActiveUserAccountCodeMatchToUserActualRegisterEmail = "SELECT users_services_scheme.code_verification.code" +
//                " FROM users_services_scheme.code_verification INNER JOIN " +
//                " users_services_scheme.user_my_chat ON  " +
//                "users_services_scheme.user_my_chat.email = '" + CorrectRequestData.USER_REGISTER_DATA.email() + "' ";
//
//
//        Flux<String> userActiveAccountCode = databaseClient.sql(sqlSelectActiveUserAccountCodeMatchToUserActualRegisterEmail)
//                .map((row, metadata) ->
//                        row.get("code", String.class)
//                )
//                .all();
//
//        StepVerifier.create(userActiveAccountCode)
//                .expectNextMatches(code -> !code.isEmpty())
//                .expectComplete()
//                .verify();
//
//
//    }
//
//
//    @Test
//    public void whenUserRegistrationDataIsValidUserShouldBeCreated() throws URISyntaxException {
//        //given
//        int USER_ROLE_ID = 1;
//
//        //when
//        createUserAccountWithNotActiveAccount(CorrectRequestData.USER_REGISTER_DATA);
//
//        //then
//        String sqlSelectAllUsersWithEmail = "SELECT id, name,surname,email,password,id_role,is_active_account FROM users_services_scheme.user_my_chat WHERE email = '" + CorrectRequestData.USER_REGISTER_DATA.email() + "'";
//        Flux<UserMyChat> userWithMatchesEmail = databaseClient.sql(sqlSelectAllUsersWithEmail)
//                .map((row, metadata) -> new UserMyChat(
//                        row.get("id", Long.class),
//                        row.get("name", String.class),
//                        row.get("surname", String.class),
//                        row.get("email", String.class),
//                        row.get("password", String.class),
//                        row.get("id_role", Integer.class),
//                        row.get("is_active_account", Boolean.class)
//                ))
//                .all();
//
//        //check if user is in database
//        StepVerifier.create(userWithMatchesEmail)
//                .expectNextMatches(user -> user.name().equals(CorrectRequestData.USER_REGISTER_DATA.name())
//                        && user.surname().equals(CorrectRequestData.USER_REGISTER_DATA.surname())
//                        && user.email().equals(CorrectRequestData.USER_REGISTER_DATA.email())
//                        && !user.password().isEmpty()
//                        && user.idRole() == USER_ROLE_ID
//                        && !user.isActiveAccount()
//                )
//                .expectComplete()
//                .verify();
//
//
//    }
//
//
//    @Test
//    public void ifAnyRegistrationDataElementHasNullValueRequestShouldReturnAnError() throws URISyntaxException {
//        //given
//        UserRegisterData userRegisterDataNullName = new UserRegisterData(null, "Walker", "correctMail@format.eu", "password");
//        UserRegisterData userRegisterDataNullSurname = new UserRegisterData("John", null, "correctMail@format.eu", "password");
//        UserRegisterData userRegisterDataNullEmail = new UserRegisterData("John", "Walker", null, "password");
//        UserRegisterData userRegisterDataNullPassword = new UserRegisterData("John", "Walker", "correctMail@format.eu", null);
//
//        List<UserRegisterData> usersRegisterDataWithNullElement = List.of(userRegisterDataNullName, userRegisterDataNullSurname, userRegisterDataNullEmail, userRegisterDataNullPassword);
//
//
//        //when
//        //then
//        for (UserRegisterData nullRegisterData : usersRegisterDataWithNullElement) {
//            webTestClient.post().uri(createRequestUtil().createRequestRegister())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.fromValue(nullRegisterData))
//                    .exchange()
//                    .expectStatus().is4xxClientError()
//                    .expectBody();
//        }
//
//
//        Flux<Long> userIdDataFlux = databaseClient.sql(sqlSelectIdsAllUsers)
//                .map((row, metadata) ->
//                        row.get("id", Long.class)
//                )
//                .all();
//
//        StepVerifier.create(userIdDataFlux)
//                .expectNextCount(expectZeroUsersInDatabase)
//                .expectComplete()
//                .verify();
//
//
//    }
//
//    @Test
//    public void ifAnyRegistrationDataElementHasEmptyValueRequestShouldReturnAnError() throws URISyntaxException {
//        //given
//        UserRegisterData userRegisterDataNullName = new UserRegisterData("", "Walker", "correctMail@format.eu", "password");
//        UserRegisterData userRegisterDataNullSurname = new UserRegisterData("John", "", "correctMail@format.eu", "password");
//        UserRegisterData userRegisterDataNullEmail = new UserRegisterData("John", "Walker", "", "password");
//        UserRegisterData userRegisterDataNullPassword = new UserRegisterData("John", "Walker", "correctMail@format.eu", "");
//
//        List<UserRegisterData> usersRegistrationDataWithEmptyElements = List.of(userRegisterDataNullName, userRegisterDataNullSurname, userRegisterDataNullEmail, userRegisterDataNullPassword);
//
//        //when
//        //then
//        for (UserRegisterData nullRegisterData : usersRegistrationDataWithEmptyElements) {
//            webTestClient.post().uri(createRequestUtil().createRequestRegister())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.fromValue(nullRegisterData))
//                    .exchange()
//                    .expectStatus().is4xxClientError()
//                    .expectBody();
//        }
//
//
//        Flux<Long> userIdDataFlux = databaseClient.sql(sqlSelectIdsAllUsers)
//                .map((row, metadata) ->
//                        row.get("id", Long.class)
//                )
//                .all();
//
//
//        StepVerifier.create(userIdDataFlux)
//                .expectNextCount(expectZeroUsersInDatabase)
//                .expectComplete()
//                .verify();
//
//
//    }
//
//
//    @Test
//    public void ifEmailIsInBadFormatRequestShouldReturnAnError() throws URISyntaxException {
//        //given
//        UserRegisterData userRegisterDataBadEmailFormat = new UserRegisterData("John", "Walker", "badEmailFormat", "password");
//
//        //when
//        webTestClient.post().uri(createRequestUtil().createRequestRegister())
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(userRegisterDataBadEmailFormat))
//                .exchange()
//                .expectStatus().is4xxClientError()
//                .expectBody();
//
//        //then
//        String sqlSelectIdsAllUsers = "SELECT id FROM users_services_scheme.user_my_chat";
//        Flux<Long> userFlux = databaseClient.sql(sqlSelectIdsAllUsers)
//                .map((row, metadata) ->
//                        row.get("id", Long.class)
//                )
//                .all();
//
//
//        StepVerifier.create(userFlux)
//                .expectNextCount(expectZeroUsersInDatabase)
//                .expectComplete()
//                .verify();
//
//
//    }
//
//    @Test
//    public void ifRequestIsSentWithoutBodyResponseCodeShouldReturnAnError() throws URISyntaxException {
//        //when
//        webTestClient.post().uri(createRequestUtil().createRequestRegister())
//                .contentType(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().is4xxClientError()
//                .expectBody();
//
//
//        //then
//        Flux<Long> userIdFlux = databaseClient.sql(sqlSelectIdsAllUsers)
//                .map((row, metadata) ->
//                        row.get("id", Long.class)
//                )
//                .all();
//
//        StepVerifier.create(userIdFlux)
//                .expectNextCount(expectZeroUsersInDatabase)
//                .expectComplete()
//                .verify();
//
//
//    }

}
