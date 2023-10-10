package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.port.in.ChangePasswordUseCase;
import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.service.message.UserErrorMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangePasswordService implements ChangePasswordUseCase {
    UserRepositoryPort userRepositoryPort;
    ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort;

    HashPasswordPort hashPasswordPort;

    private static final String USER_OR_RESET_PASSWORD_CODE_NOT_FOUND = "User or reset password code not found";

    private static final String BAD_CHANGE_PASSWORD_CODE = "Bad change password code";

    ChangePasswordService(UserRepositoryPort userRepositoryPort, ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort, HashPasswordPort hashPasswordPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.hashPasswordPort = hashPasswordPort;
        this.resetPasswordCodeRepositoryPort = resetPasswordCodeRepositoryPort;
    }

    @Override
    public Mono<Result<Status>> changePassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono) {
        return userEmailAndCodeAndPasswordMono.flatMap(userEmailCodeAndPassword -> userRepositoryPort.findUserWithEmail(userEmailCodeAndPassword.email()).
                flatMap(userFromDb -> resetPasswordCodeRepositoryPort.findResetPasswordCodeForUser(new IdUserData(userFromDb.id())).flatMap(code -> {
                    if (code.code().equals(userEmailCodeAndPassword.code())) {
                        String newPassword = hashPasswordPort.cryptPassword(userEmailCodeAndPassword.password());
                        return userRepositoryPort.changePassword(userFromDb.id(), newPassword).then(Mono.defer(() -> resetPasswordCodeRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(userFromDb.id())).
                                thenReturn(Result.success(new Status(true)))));
                    } else {
                        return Mono.just(Result.<Status>error(BAD_CHANGE_PASSWORD_CODE));
                    }
                }).switchIfEmpty(Mono.just(Result.<Status>error(USER_OR_RESET_PASSWORD_CODE_NOT_FOUND)))).switchIfEmpty(Mono.just(Result.<Status>error(UserErrorMessage.USER_NOT_FOUND.getMessage()))));
    }
}
