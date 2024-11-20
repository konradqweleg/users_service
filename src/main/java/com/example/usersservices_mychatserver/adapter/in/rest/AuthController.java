package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ConvertToJSON;
import com.example.usersservices_mychatserver.entity.request.UserAuthorizeData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/users")
public class AuthController {
    private final UserPort userPort;
    public AuthController(UserPort userPort) {
        this.userPort = userPort;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody @Valid UserRegisterDataDTO registerData) {
        return userPort.registerUser(registerData)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")))
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed")));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> authorizeUser(@RequestBody @Valid Mono<UserAuthorizeData> user) {
        return userPort.authorizeUser(user).flatMap(ConvertToJSON::convert);
    }
}
