package com.example.usersservices_mychatserver.integration.integration;

import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.integration.integration.exampleDataRequest.CorrectRequestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

public class CheckIsUserWithProvidedEmailExistsTests extends DefaultTestConfiguration  {
    @Test
    public void whenUserExistsRequestShouldReturnInformationUserWithProvidedEmailExists() throws Exception {
        //given
        createActivatedUserAccount(CorrectRequestData.USER_REGISTER_DATA);
        UserEmailData userEmailData = new UserEmailData(CorrectRequestData.USER_REGISTER_DATA.email());
        //when
        //then
        webTestClient.post().uri(createRequestUtil().createRequestCheckIsUserWithProvidedEmailExists())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userEmailData))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.correctResponse").isEqualTo(true);

    }
}
