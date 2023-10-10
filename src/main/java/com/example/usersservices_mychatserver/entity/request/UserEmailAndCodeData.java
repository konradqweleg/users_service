package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.NotNull;

public record UserEmailAndCodeData(@NotNull String email, @NotNull String code) {
}
