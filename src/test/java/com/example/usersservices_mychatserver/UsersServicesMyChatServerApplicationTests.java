package com.example.usersservices_mychatserver;

import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
//@Sql(value = "/clearAllDb.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UsersServicesMyChatServerApplicationTests {
    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int serverPort;

    @Test
    void contextLoads() {
    }

    private URI createRequestRegister() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/register");
    }

    @Test
    public void registerUserTest() throws URISyntaxException {

//
//        User user = new User();
//        user.setUsername("test_user");
//        user.setEmail("test@example.com");
//
//        webTestClient.post().uri("/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(user))
//                .exchange()
//                .expectStatus().isCreated()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .jsonPath("$.username").isEqualTo("test_user")
//                .jsonPath("$.email").isEqualTo("test@example.com");
//
//        // Dodatkowa weryfikacja, czy użytkownik został dodany do bazy danych
//        userRepository.findByUsername("test_user")
//                .as(StepVerifier::create)
//                .expectNextMatches(foundUser -> foundUser.getEmail().equals("test@example.com"))
//                .verifyComplete();


        //given
        UserRegisterData userRegisterData = new UserRegisterData("Jan", "Kowalski", "email@email.p2l","password");
        //Kod odpowiedzi, typ odpowidzi
        webTestClient.post().uri(createRequestRegister())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRegisterData))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo("true");

    }

}
