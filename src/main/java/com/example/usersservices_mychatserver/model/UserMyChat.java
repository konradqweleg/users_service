package com.example.usersservices_mychatserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public record UserMyChat(@Id Long id, String name, String surname, String email, String password,int idRole) {
}
