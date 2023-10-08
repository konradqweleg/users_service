package com.example.usersservices_mychatserver.model;

import org.springframework.data.annotation.Id;

public record ResetPasswordCode (@Id Long id, long idUser, String code) {
}
