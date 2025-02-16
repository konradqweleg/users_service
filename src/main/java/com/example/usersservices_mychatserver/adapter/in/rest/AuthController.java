package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.response.ResponseUtil;
import com.example.usersservices_mychatserver.entity.request.LoginDataDTO;
import com.example.usersservices_mychatserver.entity.request.RefreshTokenDTO;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserAccessData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/refresh-token")
    public Mono<ResponseEntity<UserAccessData>> refreshAccessToken(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
        return ResponseUtil.toResponseEntity(userPort.refreshAccessToken(refreshTokenDTO), HttpStatus.OK);
    }

}
