package com.example.usersservices_mychatserver.adapter.out.services.keycloakadapter;

import com.example.usersservices_mychatserver.entity.request.LoginData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import com.example.usersservices_mychatserver.entity.response.Status;
import org.springframework.http.HttpStatus;

@Service
public class UserKeyCloakAdapter implements UserAuthPort {

    private final String keycloakClientId = "mychat-client";
    private final String realName = "my-chat-realm";
    private final String keycloakGrantType = "password";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Keycloak keycloakAdmin;

    private final int HTTP_CREATED = 201;
    private final int HTTP_CONFLICT = 409;
    @Value("${keycloak.server.url}")
    private String keycloakUrl;

    private static final Logger logger = LogManager.getLogger(UserKeyCloakAdapter.class);

    public UserKeyCloakAdapter(Keycloak keycloakAdmin) {
        this.keycloakAdmin = keycloakAdmin;
    }

    @Override
    public Mono<UserAccessData> authorizeUser(LoginData userAuthorizeData) {
        MultiValueMap<String, String> mapAuthData = new LinkedMultiValueMap<>();
        mapAuthData.add("client_id", keycloakClientId);
        mapAuthData.add("username", userAuthorizeData.email());
        mapAuthData.add("password", userAuthorizeData.password());
        mapAuthData.add("grant_type", keycloakGrantType);

        String uriAuthorizeUser = String.format("%s/realms/MyChatApp/protocol/openid-connect/token", keycloakUrl);

        return WebClient.create()
                .post()
                .uri(uriAuthorizeUser)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(mapAuthData)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> {
                    logger.error("Bad request to register Keycloak API. Status code: {}", clientResponse.statusCode());
                    return Mono.error(new AuthServiceException("Bad request to register Keycloak API."));
                })
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        UserAccessData userAccessData = objectMapper.readValue(response, UserAccessData.class);
                        return Mono.just(userAccessData);
                    } catch (Exception e) {
                        logger.error("Error parsing response from Keycloak API: {}", e.getMessage());
                        return Mono.error(new AuthServiceException("Error parsing response from Keycloak API"));
                    }
                })
                .doOnSuccess(userAccessData -> logger.info("Successfully authorized user"));
    }


    private UserRepresentation createUserRepresentation(UserRegisterDataDTO userRegisterDataDTO) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(false);
        userRepresentation.setUsername(userRegisterDataDTO.email());
        userRepresentation.setEmail(userRegisterDataDTO.email());
        userRepresentation.setFirstName(userRegisterDataDTO.name());
        userRepresentation.setLastName(userRegisterDataDTO.surname());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRegisterDataDTO.password());
        credential.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credential));

        return userRepresentation;
    }

    @Override
    public Mono<Void> register(UserRegisterDataDTO userRegisterDataDTO) {

        return Mono.fromCallable(() -> createUserRepresentation(userRegisterDataDTO))
                .flatMap(userRepresentation -> {
                    try (Response response = keycloakAdmin.realm(realName).users().create(userRepresentation)) {
                        if (response.getStatus() == HttpStatus.CREATED.value()) {
                            logger.info("User registered successfully in Keycloak: {}", userRepresentation.getUsername());
                            return Mono.empty();
                        } else if (response.getStatus() == HttpStatus.CONFLICT.value()) {
                            logger.error("User already exists in Keycloak: {}.", userRepresentation.getUsername());
                            return Mono.error(new AuthServiceException("User already exists"));
                        } else {
                            logger.error("Failed to register user in Keycloak: {}. Response status: {}", userRepresentation.getUsername(), response.getStatus());
                            return Mono.error(new AuthServiceException("Error during user registration"));
                        }
                    } catch (Exception e) {
                        logger.error("Exception occurred during user registration in Keycloak: {}", e.getMessage(), e);
                        return Mono.error(new AuthServiceException(e));
                    }
                });
    }

    @Override
    public Mono<Status> activateUserAccount(Mono<String> emailMono) {
        return emailMono.flatMap(email -> {
            List<UserRepresentation> users = keycloakAdmin.realm(realName).users().search(email);

            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);
                user.setEnabled(true);

                try {
                    keycloakAdmin.realm(realName).users().get(user.getId()).update(user);
                    logger.info("User enabled successfully: {}", user.getUsername());
                    return Mono.just(new Status(true));
                } catch (Exception e) {
                    logger.error("Failed to enable user: {}. Error: {}", user.getUsername(), e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to enable user account"));
                }

            } else {
                logger.error("User not found with email: {}", email);
                return Mono.error(new RuntimeException("Failed to enable user account"));
            }
        });
    }

    @Override
    public Mono<Boolean> isActivatedUserAccount(String email) {
        try {
            List<UserRepresentation> users = keycloakAdmin.realm(realName).users().search(email);
            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);
                logger.info("Successfully got user account status: {}", user.isEnabled());
                return Mono.just(user.isEnabled());
            } else {
                logger.error("User not found with email: {}", email);
                return Mono.error(new AuthServiceException("User not found"));
            }
        } catch (Exception e) {
            logger.error("An error occurred while trying to get user account status for email: {}", email, e);
            return Mono.error(new AuthServiceException("Failed to get user account status", e));
        }
    }

    @Override
    public Mono<Status> changeUserPassword(Mono<String> email, String newPassword) {
        return email.flatMap(emailStr -> {
            try {
                List<UserRepresentation> users = keycloakAdmin.realm(realName).users().search(emailStr);

                if (!users.isEmpty()) {

                    UserRepresentation user = users.get(0);

                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(newPassword);
                    credential.setTemporary(false);

                    keycloakAdmin.realm(realName).users().get(user.getId()).resetPassword(credential);

                    logger.info("Password successfully changed for user with email: {}", emailStr);
                    return Mono.just(new Status(true));
                } else {
                    logger.error("User not found with email: {}", emailStr);
                    return Mono.error(new RuntimeException("User not found for email: " + emailStr));
                }
            } catch (Exception e) {
                logger.error("An error occurred while changing the password for email: {}", emailStr, e);
                return Mono.error(new RuntimeException("Error changing password", e));
            }
        });
    }
}
