package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.NotNull;

public record UserEmailDataDTO(@NotNull String email) {
}
