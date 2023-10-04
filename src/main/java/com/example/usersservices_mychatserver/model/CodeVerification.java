package com.example.usersservices_mychatserver.model;
import org.springframework.data.annotation.Id;

public record CodeVerification(@Id Long id, long idUser, String code) {
}
