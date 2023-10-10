package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.IdUserData;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.entity.Status;
import com.example.usersservices_mychatserver.port.in.ChangePasswordUseCase;
import com.example.usersservices_mychatserver.port.out.logic.HashPassword;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangePasswordService implements ChangePasswordUseCase {
    UserRepositoryPort userRepositoryPort;
    ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort;

    HashPassword hashPassword;

    ChangePasswordService(UserRepositoryPort userRepositoryPort, ResetPasswordCodeRepositoryPort resetPasswordCodeRepositoryPort, HashPassword hashPassword) {
        this.userRepositoryPort = userRepositoryPort;
        this.hashPassword = hashPassword;
        this.resetPasswordCodeRepositoryPort = resetPasswordCodeRepositoryPort;
    }

    @Override
    public Mono<Result<Status>> changePassword(Mono<ChangePasswordData> userEmailAndCodeAndPasswordMono) {
        return userEmailAndCodeAndPasswordMono.flatMap(userEmailCodeAndPassword -> userRepositoryPort.findUserWithEmail(userEmailCodeAndPassword.email()).
                flatMap(userFromDb -> resetPasswordCodeRepositoryPort.findResetPasswordCodeForUser(new IdUserData(userFromDb.id())).flatMap(code -> {
                    if (code.code().equals(userEmailCodeAndPassword.code())) {
                        String newPassword = hashPassword.cryptPassword(userEmailCodeAndPassword.password());
                        return userRepositoryPort.changePassword(userFromDb.id(), newPassword).then(Mono.defer(() -> resetPasswordCodeRepositoryPort.deleteResetPasswordCodeForUser(new IdUserData(userFromDb.id())).thenReturn(Result.success(new Status(true)))));
                    } else {
                        return Mono.just(Result.<Status>error("Bad code"));
                    }
                }).switchIfEmpty(Mono.just(Result.<Status>error("User or reset password code not found")))).switchIfEmpty(Mono.just(Result.<Status>error("User not found"))));
    }
}
