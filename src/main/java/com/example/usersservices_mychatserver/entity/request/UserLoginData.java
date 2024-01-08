package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.NotNull;

public record UserLoginData(@NotNull String email) {
}
