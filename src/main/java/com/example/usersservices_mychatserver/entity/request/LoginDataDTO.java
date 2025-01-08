package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.NotNull;

public record LoginDataDTO(@NotNull String email, @NotNull String password) {
}
