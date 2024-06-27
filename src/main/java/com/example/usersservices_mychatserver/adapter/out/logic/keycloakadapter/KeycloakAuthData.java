package com.example.usersservices_mychatserver.adapter.out.logic.keycloakadapter;

import jakarta.validation.constraints.NotNull;

public record KeycloakAuthData(
       @NotNull String clientId,
       @NotNull String username,
       @NotNull String password,
       @NotNull  String grantType
) {
}