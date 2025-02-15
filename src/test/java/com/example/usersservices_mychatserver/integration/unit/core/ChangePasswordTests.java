package com.example.usersservices_mychatserver.integration.unit.core;

import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.exception.password_reset.BadResetPasswordCodeException;
import com.example.usersservices_mychatserver.exception.password_reset.UserToResetPasswordDoesNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChangePasswordTests extends BaseTests {
    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    public void setup() {
        cleanAllDatabase(databaseClient);
    }

    @Test
    public void whenDataIsCorrectThenChangePassword() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);
        String newPassword = "newPassword";
        when(userAuthPort.changeUserPassword(userRegisterData.email(), newPassword)).thenReturn(Mono.empty());

        String userResetPasswordCode = "123456";

        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        when(generateRandomCodePort.generateCode()).thenReturn(userResetPasswordCode);

        Mono<Void> resultSendResetPasswordCode = userPort.sendResetPasswordCode(emailData);

        StepVerifier.create(resultSendResetPasswordCode)
                .expectComplete()
                .verify();

        // when
        ChangePasswordData changePasswordData = new ChangePasswordData(userRegisterData.email(), userResetPasswordCode, newPassword);
        Mono<Void> result = userPort.changeUserPassword(changePasswordData);

        // then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        Mockito.verify(userAuthPort, Mockito.times(1)).changeUserPassword(userRegisterData.email(), newPassword);

    }

    @Test
    public void whenUserToResetPasswordDoesNotExistsShouldReturnUserToResetPasswordDoesNotExistsException() {
        // when
        ChangePasswordData changePasswordDataNoExistsUser = new ChangePasswordData("no@exists.mail", "code123", "newPassword");
        Mono<Void> result = userPort.changeUserPassword(changePasswordDataNoExistsUser);

        // then
        StepVerifier.create(result)
                .expectError(UserToResetPasswordDoesNotExistsException.class)
                .verify();
    }

    @Test
    public void whenCodeIsIncorrectShouldReturnBadResetPasswordCodeException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);
        String newPassword = "newPassword";
        when(userAuthPort.changeUserPassword(userRegisterData.email(), newPassword)).thenReturn(Mono.empty());

        String userResetPasswordCode = "123456";

        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        when(generateRandomCodePort.generateCode()).thenReturn(userResetPasswordCode);

        Mono<Void> resultSendResetPasswordCode = userPort.sendResetPasswordCode(emailData);

        StepVerifier.create(resultSendResetPasswordCode)
                .expectComplete()
                .verify();

        // when
        String badResetPasswordCode = "badCode";
        ChangePasswordData changePasswordData = new ChangePasswordData(userRegisterData.email(), badResetPasswordCode, newPassword);
        Mono<Void> result = userPort.changeUserPassword(changePasswordData);

        // then
        StepVerifier.create(result)
                .expectError(BadResetPasswordCodeException.class)
                .verify();

        Mockito.verify(userAuthPort, Mockito.never()).changeUserPassword(userRegisterData.email(), newPassword);
    }


    @Test
    public void whenChangeUserPasswordInAuthServiceFailShouldReturnAuthServiceException() {
        // given
        UserRegisterDataDTO userRegisterData = new UserRegisterDataDTO("root", "surname", "mail@mail.pl", "password");
        fullRegisterAndActivateUserAccount(userRegisterData);
        String newPassword = "newPassword";

        String userResetPasswordCode = "123456";

        UserEmailDataDTO emailData = new UserEmailDataDTO(userRegisterData.email());
        when(generateRandomCodePort.generateCode()).thenReturn(userResetPasswordCode);
        when(userAuthPort.changeUserPassword(userRegisterData.email(), newPassword)).thenReturn(Mono.error(new AuthServiceException("Change password error in auth service")));

        Mono<Void> resultSendResetPasswordCode = userPort.sendResetPasswordCode(emailData);

        StepVerifier.create(resultSendResetPasswordCode)
                .expectComplete()
                .verify();

        // when
        ChangePasswordData changePasswordData = new ChangePasswordData(userRegisterData.email(), userResetPasswordCode, newPassword);
        Mono<Void> result = userPort.changeUserPassword(changePasswordData);

        // then
        StepVerifier.create(result)
                .expectError(AuthServiceException.class)
                .verify();

    }

}
