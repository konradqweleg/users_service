package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.entity.request.LoginAndPasswordData;
import com.example.usersservices_mychatserver.port.in.LogInUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper ;
    private final LogInUseCase logInUseCase;

    public LogInController(ObjectMapper objectMapper, LogInUseCase logInUseCase) {
        this.objectMapper = objectMapper;
        this.logInUseCase = logInUseCase;
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> logIn(@RequestBody @Valid Mono<LoginAndPasswordData> user) {

        return logInUseCase.logIn(user).flatMap(loggedUser ->{
            if(loggedUser.isError()){
                return Mono.just(ResponseEntity.badRequest().body(loggedUser.getError()));
            }else{
                try {
                    String loggedUserDataJSON  = objectMapper.writeValueAsString(loggedUser.getValue());
                    return Mono.just(ResponseEntity.ok(loggedUserDataJSON));
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error"));
                }

            }

        });


    }
}
