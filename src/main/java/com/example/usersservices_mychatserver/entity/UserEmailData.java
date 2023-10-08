package com.example.usersservices_mychatserver.entity;

import jakarta.validation.constraints.NotNull;

public record UserEmailData(@NotNull String email) {
}
