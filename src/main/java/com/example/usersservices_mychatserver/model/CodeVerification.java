package com.example.usersservices_mychatserver.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

public record CodeVerification(@Id Long id, @NotNull long idUser, @NotNull String code) {
}
