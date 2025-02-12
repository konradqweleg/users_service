package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetUserMatchingNameAndSurnameTests extends BaseTests{

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }

    @Test
    public void whenOneUserMatchToCriteriaShouldReturnOneUser() {
        //given
        UserRegisterDataDTO userRegisterData1 = new UserRegisterDataDTO("Kamil", "Podobiński", "mail@mail.pl", "password");
        UserRegisterDataDTO userRegisterData2 = new UserRegisterDataDTO("John", "Doe", "john.doe@mail.com", "password");
        UserRegisterDataDTO userRegisterData3 = new UserRegisterDataDTO("Jane", "Smith", "jane.smith@mail.com", "password");

        fullRegisterAndActivateUserAccount(userRegisterData1);
        fullRegisterAndActivateUserAccount(userRegisterData2);
        fullRegisterAndActivateUserAccount(userRegisterData3);

        //when
        Flux<UserData> result = userPort.getUserMatchingNameAndSurname("Kamil");

        //then
        StepVerifier.create(result)
                .expectNextMatches(userData ->
                        userData.id() != null &&
                                userData.name().equals(userRegisterData1.name()) &&
                                userData.surname().equals(userRegisterData1.surname()) &&
                                userData.email().equals(userRegisterData1.email())
                )
                .verifyComplete();
    }


    @Test
    public void whenNoUserMatchesCriteriaShouldReturnEmpty() {
        //given
        UserRegisterDataDTO userRegisterData1 = new UserRegisterDataDTO("John", "Doe", "john.doe@mail.com", "password");
        UserRegisterDataDTO userRegisterData2 = new UserRegisterDataDTO("Jane", "Smith", "jane.smith@mail.com", "password");

        fullRegisterAndActivateUserAccount(userRegisterData1);
        fullRegisterAndActivateUserAccount(userRegisterData2);

        //when
        Flux<UserData> result = userPort.getUserMatchingNameAndSurname("Kamil");

        //then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void whenMultipleUsersMatchCriteriaShouldReturnAllMatchingUsers() {
        //given
        UserRegisterDataDTO userRegisterData1 = new UserRegisterDataDTO("Kamil", "Podobiński", "mail1@mail.pl", "password");
        UserRegisterDataDTO userRegisterData2 = new UserRegisterDataDTO("Kamil", "Nowak", "mail2@mail.pl", "password");
        UserRegisterDataDTO userRegisterData3 = new UserRegisterDataDTO("Jane", "Smith", "jane.smith@mail.com", "password");

        fullRegisterAndActivateUserAccount(userRegisterData1);
        fullRegisterAndActivateUserAccount(userRegisterData2);
        fullRegisterAndActivateUserAccount(userRegisterData3);

        //when
        Flux<UserData> result = userPort.getUserMatchingNameAndSurname("Kamil");

        //then
        StepVerifier.create(result)
                .expectNextMatches(userData ->
                        userData.id() != null &&
                                userData.name().equals(userRegisterData1.name()) &&
                                userData.surname().equals(userRegisterData1.surname()) &&
                                userData.email().equals(userRegisterData1.email())
                )
                .expectNextMatches(userData ->
                        userData.id() != null &&
                                userData.name().equals(userRegisterData2.name()) &&
                                userData.surname().equals(userRegisterData2.surname()) &&
                                userData.email().equals(userRegisterData2.email())
                )
                .verifyComplete();
    }

    @Test
    public void whenUserMatchesFullNameCriteriaShouldReturnUser() {
        //given
        UserRegisterDataDTO userRegisterData1 = new UserRegisterDataDTO("Jan", "Polak", "jan.polak@mail.com", "password");
        UserRegisterDataDTO userRegisterData2 = new UserRegisterDataDTO("John", "Doe", "john.doe@mail.com", "password");

        fullRegisterAndActivateUserAccount(userRegisterData1);
        fullRegisterAndActivateUserAccount(userRegisterData2);

        //when
        Flux<UserData> result = userPort.getUserMatchingNameAndSurname("Jan Polak");

        //then
        StepVerifier.create(result)
                .expectNextMatches(userData ->
                        userData.id() != null &&
                                userData.name().equals(userRegisterData1.name()) &&
                                userData.surname().equals(userRegisterData1.surname()) &&
                                userData.email().equals(userRegisterData1.email())
                )
                .verifyComplete();
    }

    @Test
    public void whenNoUserMatchesFullNameCriteriaShouldReturnEmpty() {
        //given
        UserRegisterDataDTO userRegisterData1 = new UserRegisterDataDTO("John", "Doe", "john.doe@mail.com", "password");
        UserRegisterDataDTO userRegisterData2 = new UserRegisterDataDTO("Jane", "Smith", "jane.smith@mail.com", "password");

        fullRegisterAndActivateUserAccount(userRegisterData1);
        fullRegisterAndActivateUserAccount(userRegisterData2);

        //when
        Flux<UserData> result = userPort.getUserMatchingNameAndSurname("Jan Polak");

        //then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
