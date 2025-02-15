package com.example.usersservices_mychatserver.adapter.in.rest.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ResponseUtil {

    public static <T> Mono<ResponseEntity<T>> toResponseEntity(Mono<T> mono, HttpStatus status) {
        return mono.map(body -> ResponseEntity.status(status).body(body));
    }

    public static <T> Mono<ResponseEntity<List<T>>> toResponseEntity(Flux<T> flux, HttpStatus status) {
        return flux.collectList()
                .map(body -> ResponseEntity.status(status).body(body));
    }


}