package com.example.usersservices_mychatserver.adapter.out.logic.keycloakadapter;

import com.example.usersservices_mychatserver.entity.request.UserAuthorizeData;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.port.out.services.UserAuthPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserKeyCloakAdapter implements UserAuthPort {

    @Value("${keyclock.admin.username}")
    private String username;

    @Value("${keyclock.admin.password}")
    private String password;
    private final String uriAuthorizeUser = "http://localhost:8080/realms/MyChatApp/protocol/openid-connect/token";

    private final String keycloakClientId = "mychatclient";
    private final String keycloakGrantType = "password";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Mono<UserAccessData> authorizeUser(Mono<UserAuthorizeData> userAuthorizeData) {

        Mono<MultiValueMap<String, String>> formData = userAuthorizeData.map(data -> {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", keycloakClientId);
            map.add("username", data.email());
            map.add("password", data.password());
            map.add("grant_type", keycloakGrantType);
            return map;
        });

        return WebClient.create().post().uri(uriAuthorizeUser)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData, MultiValueMap.class)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        return Mono.just(objectMapper.readValue(response, UserAccessData.class));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException(e));
                    }
                });
    }

    @Override
    public Mono<UserAccessData> getAdminAccessData() {


        Mono<MultiValueMap<String, String>> formData = Mono.just(new LinkedMultiValueMap<>())
                .map(map -> {
                    MultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
                    map2.add("client_id", keycloakClientId);
                    map2.add("username", username);
                    map2.add("password", password);
                    map2.add("grant_type", keycloakGrantType);
                    return map2;
                });



        return WebClient.create().post().uri(uriAuthorizeUser)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData, MultiValueMap.class)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        System.out.println(response);
                        return Mono.just(objectMapper.readValue(response, UserAccessData.class));
                    } catch (Exception e) {
                        System.out.println(e.getMessage()   );
                        return Mono.error(new RuntimeException(e));
                    }
                });
    }
}
