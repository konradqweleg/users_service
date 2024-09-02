package com.example.usersservices_mychatserver.config.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class KeyCloakConfiguration {

    @Value("${keycloak.admin.username}")
    private String usernameKeycloakAdmin;

    @Value("${keycloak.admin.password}")
    private String passwordKeycloakAdmin;

    @Value("${keycloak.server.url}")
    private String serverUrlKeycloak;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrlKeycloak)
                .realm("master")
                .clientId("admin-cli")
                .clientSecret("client-secret")
                .username("admin")
                .password("admin")
                .grantType("password")
                .build();
    }


}
