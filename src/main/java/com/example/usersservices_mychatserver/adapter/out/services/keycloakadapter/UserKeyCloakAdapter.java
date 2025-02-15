package com.example.usersservices_mychatserver.adapter.out.services.keycloakadapter;

import com.example.usersservices_mychatserver.entity.request.LoginDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.exception.auth.UnauthorizedException;
import com.example.usersservices_mychatserver.exception.auth.UserAlreadyRegisteredException;
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

import org.springframework.http.HttpStatus;

@Service
public class UserKeyCloakAdapter implements UserAuthPort {

    @Value("${keycloak.client.id}")
    private String keycloakClientId;

    @Value("${keycloak.client.realm}")
    private String realmName;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Keycloak keycloakAdmin;

    @Value("${keycloak.server.url}")
    private String keycloakUrl;

    private static final Logger logger = LogManager.getLogger(UserKeyCloakAdapter.class);

    public UserKeyCloakAdapter(Keycloak keycloakAdmin) {
        this.keycloakAdmin = keycloakAdmin;
    }

    @Override
    public Mono<UserAccessData> authorizeUser(LoginDataDTO userAuthorizeData) {
        MultiValueMap<String, String> mapAuthData = new LinkedMultiValueMap<>();
        mapAuthData.add("client_id", keycloakClientId);
        mapAuthData.add("username", userAuthorizeData.email());
        mapAuthData.add("password", userAuthorizeData.password());
        String keycloakGrantType = "password";
        mapAuthData.add("grant_type", keycloakGrantType);

        String uriAuthorizeUser = String.format("%s/realms/my-chat-realm/protocol/openid-connect/token", keycloakUrl);

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
                .onStatus(HttpStatus.UNAUTHORIZED::equals, clientResponse -> {
                    logger.error("Unauthorized request to register Keycloak API. Status code: {}", clientResponse.statusCode());
                    return Mono.error(new UnauthorizedException("User unauthorized."));
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
                    try (Response response = keycloakAdmin.realm(realmName).users().create(userRepresentation)) {
                        if (response.getStatus() == HttpStatus.CREATED.value()) {
                            logger.info("User registered successfully in Keycloak: {}", userRepresentation.getUsername());
                            return Mono.empty();
                        } else if (response.getStatus() == HttpStatus.CONFLICT.value()) {
                            logger.error("User already exists in Keycloak: {}.", userRepresentation.getUsername());
                            return Mono.error(new UserAlreadyRegisteredException("User already exists"));
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
    public Mono<Void> activateUserAccount(String email) {
        List<UserRepresentation> users = keycloakAdmin.realm(realmName).users().search(email);

        if (!users.isEmpty()) {
            UserRepresentation user = users.get(0);
            user.setEnabled(true);

            try {
                keycloakAdmin.realm(realmName).users().get(user.getId()).update(user);
                logger.info("User enabled successfully: {}", user.getUsername());
                return Mono.empty();
            } catch (Exception e) {
                logger.error("Failed to enable user: {}. Error: {}", user.getUsername(), e.getMessage(), e);
                return Mono.error(new AuthServiceException("Failed to enable user account"));
            }

        } else {
            logger.error("User not found with email: {}", email);
            return Mono.error(new AuthServiceException("Failed to enable user account"));
        }
    }

    @Override
    public Mono<Boolean> isEmailAlreadyActivatedUserAccount(String email) {
        try {
            List<UserRepresentation> users = keycloakAdmin.realm(realmName).users().search(email);
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
    public Mono<Void> changeUserPassword(String email, String newPassword) {
        try {
            List<UserRepresentation> users = keycloakAdmin.realm(realmName).users().search(email);

            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);

                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(newPassword);
                credential.setTemporary(false);

                keycloakAdmin.realm(realmName).users().get(user.getId()).resetPassword(credential);

                logger.info("Password successfully changed for user with email: {}", email);
                return Mono.empty();
            } else {
                logger.error("User not found with email: {}", email);
                return Mono.error(new AuthServiceException("User not found for email: " + email));
            }
        } catch (Exception e) {
            logger.error("An error occurred while changing the password for email: {}", email, e);
            return Mono.error(new AuthServiceException("Error changing password", e));
        }
    }

    @Override
    public Mono<Boolean> isEmailAlreadyRegistered(String email) {
        try {
            List<UserRepresentation> users = keycloakAdmin.realm(realmName).users().search(email);
            if (!users.isEmpty()) {
                logger.info("User with email: {} already registered", email);
                return Mono.just(true);
            } else {
                logger.info("User with email: {} not registered", email);
                return Mono.just(false);
            }
        } catch (Exception e) {
            logger.error("An error occurred while checking if user with email: {} is already registered", email, e);
            return Mono.error(new AuthServiceException("Error checking if user is already registered", e));
        }
    }
}
