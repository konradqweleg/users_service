package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.model.UserRegisterData;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/register")
public class RegisterController {
    private final RegisterUserUseCase registerUserUseCase;
    private final ObjectMapper objectMapper ;

    public RegisterController(RegisterUserUseCase registerUserUseCase, ObjectMapper objectMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.objectMapper = objectMapper;
    }

    @PostMapping("")
    public Mono<ResponseEntity<UserMyChat>> registerUser(@RequestBody @Valid Mono< UserRegisterData> user) {


        return registerUserUseCase.registerUser(user).flatMap(s -> Mono.just(ResponseEntity.ok(s)));

    }

    //        return registerUserUseCase.registerUser(user)
//                .flatMap(s -> {
//                    try {
//                        return Mono.just(ResponseEntity.ok(objectMapper.writeValueAsString(s)));
//
//                    } catch (JsonProcessingException e) {
//                        return Mono.error(e);
//                    }
//                }) .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().body("Wystąpił błąd: " + error.getMessage())));

}
