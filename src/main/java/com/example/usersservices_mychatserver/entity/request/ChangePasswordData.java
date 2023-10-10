package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordData(@NotNull String email,@NotNull String code,@NotNull String password) {
}
