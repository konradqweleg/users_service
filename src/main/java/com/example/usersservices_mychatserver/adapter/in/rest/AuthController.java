package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ResponseUtil;
import com.example.usersservices_mychatserver.entity.request.LoginDataDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/users")
public class AuthController {
    private final UserPort userPort;
    public AuthController(UserPort userPort) {
        this.userPort = userPort;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> registerUser(@RequestBody @Valid UserRegisterDataDTO registerData) {
        return ResponseUtil.toResponseEntity(userPort.registerUser(registerData), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<UserAccessData>> authorizeUser(@RequestBody @Valid LoginDataDTO loginDataDTO) {
        return ResponseUtil.toResponseEntity(userPort.login(loginDataDTO), HttpStatus.OK);
    }

}
