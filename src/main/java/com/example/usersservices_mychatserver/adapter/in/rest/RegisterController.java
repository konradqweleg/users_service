package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/register")
public class RegisterController {
    private final RegisterUserUseCase registerUserUseCase;

    public RegisterController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;

    }

    @PostMapping("")
    public Mono<UserMyChat> registerUser(@RequestBody Mono<UserMyChat> user) {
        return registerUserUseCase.registerUser(user);
    }

}
