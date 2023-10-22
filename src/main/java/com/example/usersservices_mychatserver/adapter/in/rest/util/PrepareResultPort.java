package com.example.usersservices_mychatserver.adapter.in.rest.util;

import com.example.usersservices_mychatserver.entity.response.Result;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface PrepareResultPort {
    public <T> Mono<ResponseEntity<String>> convert (Result<T> response);
}
