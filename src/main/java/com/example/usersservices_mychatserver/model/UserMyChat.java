package com.example.usersservices_mychatserver.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public record UserMyChat(@Id Long id, @NotNull String name,@NotNull String surname,@NotNull String email,@NotNull String password,@NotNull int idRole,@NotNull boolean isActiveAccount) {

    public UserMyChat withNewPassword(String newPassword) {
        return new UserMyChat(id(),name(),surname(),email(),newPassword,idRole(),isActiveAccount());
    }

}
