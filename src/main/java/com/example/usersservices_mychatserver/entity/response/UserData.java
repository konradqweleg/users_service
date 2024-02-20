package com.example.usersservices_mychatserver.entity.response;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

public record UserData(@Id Long id, @NotNull String name, @NotNull String surname, @NotNull String email) {
}
