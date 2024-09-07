package com.example.usersservices_mychatserver.adapter.in.rest.util;

import com.example.usersservices_mychatserver.entity.response.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ConvertToJSON {

    private static final String ERROR_CONVERSION_TO_JSON = "Error";

    public static <T> Mono<ResponseEntity<String>> convert(Result<T> response) {
        System.out.println("Status: " + response.isSuccess());

        ObjectMapper objectMapper = new ObjectMapper();
        if (response.isError()) {
            return Mono.just(ResponseEntity.badRequest().body(response.getError()));
        } else {
            try {
                String registeredUserDataJSON = objectMapper.writeValueAsString(response.getValue());
                return Mono.just(ResponseEntity.ok(registeredUserDataJSON));
            } catch (JsonProcessingException e) {
                return Mono.error(new RuntimeException(ERROR_CONVERSION_TO_JSON));
            }

        }
    }

    public static <T> Mono<ResponseEntity<String>> convert(Flux<T> response) {

        ObjectMapper objectMapper = new ObjectMapper();
        return response.collectList().map(users -> {
            try {
                String json = objectMapper.writeValueAsString(users);
                return ResponseEntity.ok().body(json);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR_CONVERSION_TO_JSON);
            }
        }).onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));


    }




}
