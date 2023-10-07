package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.entity.UserRegisterData;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<String>> registerUser(@RequestBody @Valid Mono< UserRegisterData> user) {
        return registerUserUseCase.registerUser(user).flatMap(registeredUser ->{
            if(registeredUser.isError()){
                return Mono.just(ResponseEntity.badRequest().body(registeredUser.getError()));
            }else{
                try {
                    String registeredUserDataJSON  = objectMapper.writeValueAsString(registeredUser.getValue());
                    return Mono.just(ResponseEntity.ok(registeredUserDataJSON));
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error"));
                }

            }

        });

    }


}
