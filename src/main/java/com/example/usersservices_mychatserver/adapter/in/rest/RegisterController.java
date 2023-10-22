package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.PrepareResultPort;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/register")
public class RegisterController {
    private final RegisterUserUseCase registerUserUseCase;
    private final PrepareResultPort convertObjectToJsonResponse;


    public RegisterController(RegisterUserUseCase registerUserUseCase, PrepareResultPort convertObjectToJsonResponse) {
        this.registerUserUseCase = registerUserUseCase;
        this.convertObjectToJsonResponse = convertObjectToJsonResponse;
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody @Valid Mono< UserRegisterData> user) {
        return registerUserUseCase.registerUser(user).flatMap(convertObjectToJsonResponse::convert);
    }

}
