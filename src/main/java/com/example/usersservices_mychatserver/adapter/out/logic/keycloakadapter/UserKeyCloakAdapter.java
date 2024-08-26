package com.example.usersservices_mychatserver.adapter.out.logic.keycloakadapter;

import com.example.usersservices_mychatserver.entity.request.UserAuthorizeData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.port.out.logic.StoreAdminTokensPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
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

    private final String keycloakClientId = "mychatclient";
    private final String realName = "MyChatApp";
    private final String keycloakGrantType = "password";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Keycloak keycloakAdmin;

    public UserKeyCloakAdapter(Keycloak keycloakAdmin) {
        this.keycloakAdmin = keycloakAdmin;
    }

    @Override
    public Mono<UserAccessData> authorizeUser(Mono<UserAuthorizeData> userAuthorizeData) {

        Mono<MultiValueMap<String, String>> bodyUserAuthData = userAuthorizeData.map(authorizeData -> {
            MultiValueMap<String, String> mapAuthData = new LinkedMultiValueMap<>();
            mapAuthData.add("client_id", keycloakClientId);
            mapAuthData.add("username", authorizeData.email());
            mapAuthData.add("password", authorizeData.password());
            mapAuthData.add("grant_type", keycloakGrantType);
            return mapAuthData;
        });


        String uriAuthorizeUser = "http://localhost:8080/realms/MyChatApp/protocol/openid-connect/token";
        return WebClient.create().post().uri(uriAuthorizeUser)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(bodyUserAuthData, MultiValueMap.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse ->
                        Mono.error(new RuntimeException("User not authorized"))
                )
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        return Mono.just(objectMapper.readValue(response, UserAccessData.class));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("User not authorized"));
                    }
                });
    }


    @Override
    public Mono<Status> registerNewUser(Mono<UserRegisterData> userRegisterDataMono) {

        Mono<UserRepresentation> keyCloakUserRepresentation = userRegisterDataMono.map(data -> {
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEnabled(false);
            userRepresentation.setUsername(data.email());
            userRepresentation.setEmail(data.email());
            userRepresentation.setFirstName(data.name());
            userRepresentation.setLastName(data.surname());

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(data.password());
            credential.setTemporary(false);
            userRepresentation.setCredentials(Collections.singletonList(credential));

            return userRepresentation;
        });

        return keyCloakUserRepresentation.flatMap(
                userRepresentation -> {
                    try (Response response = keycloakAdmin.realm(realName).users().create(userRepresentation)) {

                        if (response.getStatus() == 201) {

                            return Mono.just(new Status(true));
                        } else {
                            return Mono.error(new RuntimeException("User not registered"));
                        }

                    } catch (Exception e) {
                        return Mono.error(new RuntimeException(e));
                    }
                }
        );


    }

    @Override
    public Mono<Status> activateUserAccount(Mono<String> emailMono) {
        return emailMono.flatMap(email -> {
            List<UserRepresentation> users = keycloakAdmin.realm(realName).users().search(email);
            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);
                user.setEnabled(true);
                keycloakAdmin.realm(realName).users().get(user.getId()).update(user);
                return Mono.just(new Status(true));
            } else {
                return Mono.error(new RuntimeException("User not found"));
            }
        });
    }

    @Override
    public Mono<Boolean> isActivatedUserAccount(Mono<String> email) {
        return email.flatMap(emailStr -> {
            List<UserRepresentation> users = keycloakAdmin.realm(realName).users().search(emailStr);
            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);
                return Mono.just(user.isEnabled());
            } else {
                return Mono.error(new RuntimeException("User not found"));
            }
        });
    }
}
