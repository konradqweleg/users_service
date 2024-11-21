package com.example.usersservices_mychatserver.adapter.in.rest.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class ResponseUtil {

    public static <T> Mono<ResponseEntity<T>> toResponseEntity(Mono<T> mono, HttpStatus status) {
        return mono.map(body -> ResponseEntity.status(status).body(body));
    }


}