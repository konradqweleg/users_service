package com.example.usersservices_mychatserver.entity;

import org.springframework.data.annotation.Id;

public record UserMyChat(@Id Long id, String name, String surname, String email, String password) {
}
