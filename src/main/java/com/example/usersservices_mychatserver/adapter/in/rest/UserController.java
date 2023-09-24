package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.CreateUserUseCase;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;

    }

    @PostMapping("")
    public Mono<UserMyChat> saveUser(@RequestBody UserMyChat user) {
        return createUserUseCase.createUser(user);
    }

}
