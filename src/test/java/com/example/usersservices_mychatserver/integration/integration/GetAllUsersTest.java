package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import com.example.usersservices_mychatserver.integration.integration.request_util.RequestUtil;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

public class GetAllUsersTest extends DefaultTestConfiguration{

//    @Test
//    public void requestShouldReturnAllUsers() throws URISyntaxException {
//        // given
//        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);
//        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA_2);
//        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA_3);
//
//        // when
//        //then
//        webTestClient.get().uri(createRequestUtil().createRequestGetAllUsers())
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.length()").isEqualTo(3)
//                .jsonPath("$[0].email").isEqualTo(CorrectRequestData.USER_REGISTER_DATA.email())
//                .jsonPath("$[0].name").isEqualTo(CorrectRequestData.USER_REGISTER_DATA.name())
//                .jsonPath("$[0].surname").isEqualTo(CorrectRequestData.USER_REGISTER_DATA.surname())
//                .jsonPath("$[1].email").isEqualTo(CorrectRequestData.USER_REGISTER_DATA_2.email())
//                .jsonPath("$[1].name").isEqualTo(CorrectRequestData.USER_REGISTER_DATA_2.name())
//                .jsonPath("$[1].surname").isEqualTo(CorrectRequestData.USER_REGISTER_DATA_2.surname())
//                .jsonPath("$[2].email").isEqualTo(CorrectRequestData.USER_REGISTER_DATA_3.email())
//                .jsonPath("$[2].name").isEqualTo(CorrectRequestData.USER_REGISTER_DATA_3.name())
//                .jsonPath("$[2].surname").isEqualTo(CorrectRequestData.USER_REGISTER_DATA_3.surname());
//    }
//
//    @Test
//    public void whenNoExistsUsersRequestShouldReturnEmptyArray() throws URISyntaxException {
//        //when
//        //then
//        webTestClient.get().uri(createRequestUtil().createRequestGetAllUsers())
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.length()").isEqualTo(0);
//    }
}
