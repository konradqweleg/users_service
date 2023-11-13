package com.example.usersservices_mychatserver.adapter.in.rest;


import com.example.usersservices_mychatserver.adapter.in.rest.util.PrepareResultPort;
import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.LoginAndPasswordData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterData;
import com.example.usersservices_mychatserver.port.in.AuthenticationUserPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/authentication")
public class AuthenticationUserController {
    private final AuthenticationUserPort authenticationUserPort;
    private final PrepareResultPort convertObjectToJsonResponse;

    public AuthenticationUserController(AuthenticationUserPort authenticationUserPort, PrepareResultPort convertObjectToJsonResponse) {
        this.authenticationUserPort = authenticationUserPort;
        this.convertObjectToJsonResponse = convertObjectToJsonResponse;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> logIn(@RequestBody @Valid Mono<LoginAndPasswordData> user) {
        return authenticationUserPort.isCorrectCredentials(user).flatMap(convertObjectToJsonResponse::convert);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody @Valid Mono<UserRegisterData> user) {
        return authenticationUserPort.registerUser(user).flatMap(convertObjectToJsonResponse::convert);
    }

    @PostMapping("/changePassword")
    public Mono<ResponseEntity<String>> changePassword(@RequestBody @Valid Mono<ChangePasswordData> changePasswordDataMono) {
        return authenticationUserPort.changePassword(changePasswordDataMono).flatMap(convertObjectToJsonResponse::convert);
    }
}
