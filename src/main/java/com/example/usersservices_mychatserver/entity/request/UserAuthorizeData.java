package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.NotNull;

public record UserAuthorizeData(@NotNull String email, @NotNull String password) {
}
