package com.example.usersservices_mychatserver.entity;

import jakarta.validation.constraints.NotNull;

public record UserEmailAndCode(@NotNull String email, @NotNull String code) {
}
