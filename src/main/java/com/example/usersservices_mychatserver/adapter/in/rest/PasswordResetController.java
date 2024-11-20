package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ConvertToJSON;
import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RequestMapping(value = "/api/v1/users/password-reset")
public class PasswordResetController {

    private final UserPort userPort;

    public PasswordResetController(UserPort userPort) {
        this.userPort = userPort;
    }
    @PostMapping("/code")
    public Mono<ResponseEntity<String>> sendResetPasswordCode(@RequestBody @Valid Mono<UserEmailData> user) {
        return userPort.sendResetPasswordCode(user).flatMap(ConvertToJSON::convert);
    }

    @PostMapping("/validate-code")
    public Mono<ResponseEntity<String>> isCorrectResetPasswordCode(@RequestBody @Valid Mono<UserEmailAndCodeData> userEmailAndCodeMono) {
        return userPort.checkIsCorrectResetPasswordCode(userEmailAndCodeMono).flatMap(ConvertToJSON::convert);
    }
}
