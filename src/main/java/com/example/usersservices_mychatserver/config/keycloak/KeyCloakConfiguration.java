package com.example.usersservices_mychatserver.config.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class KeyCloakConfiguration {

    @Value("${keyclock.admin.username}")
    private String usernameKeycloakAdmin;

    @Value("${keyclock.admin.password}")
    private String passwordKeycloakAdmin;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("master")
                .clientId("admin-cli")
                .clientSecret("client-secret")
                .username(usernameKeycloakAdmin)
                .password(passwordKeycloakAdmin)
                .grantType("password")
                .build();
    }


}
