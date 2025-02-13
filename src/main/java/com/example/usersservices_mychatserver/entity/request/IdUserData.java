package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record IdUserData(@NotNull @Min(1) Long idUser) {

}
