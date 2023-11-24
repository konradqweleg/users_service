package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;


public record EmailAndPasswordData(@NotNull @Email String email, @NotNull String password) {
}
