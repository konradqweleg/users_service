package com.example.usersservices_mychatserver.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;


public record UserMyChat(@Id Long id, @NotNull String name,@NotNull String surname,@NotNull String email) {

}
