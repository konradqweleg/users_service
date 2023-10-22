package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.PrepareResultPort;
import com.example.usersservices_mychatserver.entity.request.LoginAndPasswordData;
import com.example.usersservices_mychatserver.port.in.LogInUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/login")
public class LogInController {
    private final PrepareResultPort convertObjectToJsonResponse;
    private final LogInUseCase logInUseCase;

    public LogInController(PrepareResultPort convertObjectToJsonResponse, LogInUseCase logInUseCase) {
        this.convertObjectToJsonResponse = convertObjectToJsonResponse;
        this.logInUseCase = logInUseCase;
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> logIn(@RequestBody @Valid Mono<LoginAndPasswordData> user) {

        return logInUseCase.logIn(user).flatMap(convertObjectToJsonResponse::convert);


    }
}
