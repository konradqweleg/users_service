package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.response.UserData;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URISyntaxException;
import java.util.Objects;


public class GetUserAboutIdTest extends DefaultTestConfiguration {

    private final String sqlSelectIdsAllUsers = "SELECT id FROM users_services_scheme.user_my_chat";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void whenUserWithProvidedIdNotExistsRequestShouldReturnErrorUserNotFound() throws URISyntaxException {

        //when
        //then
        String noExistsUserId = "7";

        webTestClient.get().uri(createRequestUtil().createRequestGetUserAboutId() + noExistsUserId)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.ErrorMessage").isEqualTo("User not found");

    }

    @Test
    public void whenUserWithProvidedIdExistsRequestShouldReturnCorrectResponse() throws URISyntaxException {
        // given
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);

        // when
        //then
        Flux<Long> userIdFluxFromDatabase = databaseClient.sql(sqlSelectIdsAllUsers)
                .map((row, metadata) -> row.get("id", Long.class))
                .all();

        Flux<UserData> resultRequestTestGetUserAboutIdForRecordFromDatabase = userIdFluxFromDatabase.flatMap(userId -> {
            try {
                return WebClient.create().get().uri(createRequestUtil().createRequestGetUserAboutId() + userId.toString())
                        .retrieve()
                        .toEntity(String.class)
                        .flatMap(responseEntity -> Mono.just(Objects.requireNonNull(responseEntity.getBody())));

            } catch (URISyntaxException e) {
                return Flux.error(new RuntimeException(e));
            }
        }).flatMap(userResponse -> {
            try {
                UserData userData = objectMapper.readValue(userResponse, UserData.class);
                return Mono.just(userData);
            } catch (JsonProcessingException e) {
                return Mono.error(new RuntimeException(e));
            }
        });

        StepVerifier.create(resultRequestTestGetUserAboutIdForRecordFromDatabase)
                .expectNextMatches(
                        userResponseResult -> userResponseResult.name().equals(CorrectRequestData.USER_REGISTER_DATA.name())
                                && userResponseResult.surname().equals(CorrectRequestData.USER_REGISTER_DATA.surname())
                                && userResponseResult.email().equals(CorrectRequestData.USER_REGISTER_DATA.email())
                )
                .expectComplete()
                .verify();


    }

}
