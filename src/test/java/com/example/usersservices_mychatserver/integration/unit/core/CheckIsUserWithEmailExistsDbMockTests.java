package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckIsUserWithEmailExistsDbMockTests {
    @Autowired
    private UserPort userPort;

    @MockBean
    private UserRepositoryPort userRepositoryPort;


    @Test
    public void whenFindUserWithEmailFailureShouldReturnSaveDataInRepositoryException() {
        // given
        when(userRepositoryPort.findUserWithEmail(any())).thenReturn(Mono.error(new SaveDataInRepositoryException("Find user error")));

        // when
        UserEmailDataDTO userEmailDataDTO = new UserEmailDataDTO("test@mail.pl");
        Mono<Boolean> result = userPort.checkIsUserWithThisEmailExist(userEmailDataDTO);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();
    }

}
