package com.example.usersservices_mychatserver.controller;

import com.example.usersservices_mychatserver.entity.UserMyChat;
import com.example.usersservices_mychatserver.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/user")
public class UserController {


    final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public Flux<UserMyChat> getUsers() {

        return userRepository.findAll();

    }

    @PostMapping("")
    public Mono<UserMyChat> postUser(@RequestBody UserMyChat book) {
        return userRepository.save(book);
    }

}
