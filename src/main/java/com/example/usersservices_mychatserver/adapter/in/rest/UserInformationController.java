package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ConvertToJSON;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserInformationController {
    private final UserPort userPort;

    public UserInformationController(UserPort userPort) {
        this.userPort = userPort;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> getUserAboutId(@PathVariable Long id) {
        return userPort.getUserAboutId(Mono.just(new IdUserData(id))).flatMap(ConvertToJSON::convert);
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<String>> getUserMatchingNameAndSurname(@RequestParam String patternName) {
        return ConvertToJSON.convert(userPort.getUserMatchingNameAndSurname(Mono.just(patternName)));
    }

    @GetMapping
    public Mono<ResponseEntity<String>> getAllUsers() {
        return ConvertToJSON.convert(userPort.getAllUsers());
    }

    @GetMapping("/email")
    public Mono<ResponseEntity<String>> getUserAboutEmail(@RequestParam String email) {
        return userPort.getUserAboutEmail(Mono.just(new UserEmailData(email))).flatMap(ConvertToJSON::convert);
    }

    @GetMapping("/existence")
    public Mono<ResponseEntity<String>> checkIsUserWithThisEmailExist(@RequestBody @Valid Mono<UserEmailData> user) {
        return userPort.checkIsUserWithThisEmailExist(user).flatMap(ConvertToJSON::convert);
    }
}
