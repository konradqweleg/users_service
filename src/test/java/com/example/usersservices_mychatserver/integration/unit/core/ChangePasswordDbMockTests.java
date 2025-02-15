package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.UserPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChangePasswordDbMockTests {

    @Autowired
    private UserPort userPort;


    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    private UserAuthPort userAuthPort;

    @Test
    public void whenFindUserWithEmailFailureShouldReturnSaveDataInRepositoryException() {
        //given
        String userEmail = "user@mail.pl";
        when(userRepositoryPort.findUserWithEmail(userEmail))
                .thenReturn(Mono.error(new SaveDataInRepositoryException("Find user error")));
        // when
        ChangePasswordData changePasswordData = new ChangePasswordData(userEmail, "userResetPasswordCode", "newPassword");
        Mono<Void> result = userPort.changeUserPassword(changePasswordData);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();

    }

    @Test
    public void whenDeleteResetPasswordCodeFailureShouldReturnSaveDataInRepositoryException() {
        //given
        UserMyChat userMyChat = new UserMyChat(1L, "root", "surname", "email@email.pl");
        when(userRepositoryPort.findUserWithEmail(userMyChat.email())).thenReturn(Mono.just(userMyChat));
        IdUserData idUserData = new IdUserData(userMyChat.id());
        String resetPasswordCode = "userResetPasswordCode";
        when(userRepositoryPort.findResetPasswordCodeForUserById(idUserData)).thenReturn(Mono.just(new ResetPasswordCode(1L, userMyChat.id(), resetPasswordCode)));
        when(userRepositoryPort.deleteResetPasswordCodeForUser(idUserData)).thenReturn(Mono.error(new SaveDataInRepositoryException("Delete reset password code error")));
        when(userAuthPort.changeUserPassword(userMyChat.email(), "newPassword")).thenReturn(Mono.empty());

        // when
        ChangePasswordData changePasswordData = new ChangePasswordData(userMyChat.email(), resetPasswordCode, "newPassword");
        Mono<Void> result = userPort.changeUserPassword(changePasswordData);

        // then
        StepVerifier.create(result)
                .expectError(SaveDataInRepositoryException.class)
                .verify();

    }
}
