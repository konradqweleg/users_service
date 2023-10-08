package com.example.usersservices_mychatserver.entity;

import jakarta.validation.constraints.NotNull;

public record LoginAndPasswordData(@NotNull String login,@NotNull String password) {
}
