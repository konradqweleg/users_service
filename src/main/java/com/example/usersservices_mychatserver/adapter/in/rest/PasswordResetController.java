package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.response.ResponseUtil;
import com.example.usersservices_mychatserver.entity.request.ChangePasswordData;
import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeDTO;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.response.IsCorrectResetPasswordCode;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public Mono<ResponseEntity<Void>> sendResetPasswordCode(@RequestBody @Valid UserEmailDataDTO emailData) {
        return ResponseUtil.toResponseEntity(userPort.sendResetPasswordCode(emailData), HttpStatus.OK);
    }

    @PostMapping("/validate-code")
    public Mono<ResponseEntity<IsCorrectResetPasswordCode>> isCorrectResetPasswordCode(@RequestBody @Valid UserEmailAndCodeDTO userEmailAndCodeMono) {
        return ResponseUtil.toResponseEntity(userPort.checkIsCorrectResetPasswordCode(userEmailAndCodeMono), HttpStatus.OK);
    }

    @PostMapping("/password-reset/change")
    public Mono<ResponseEntity<Void>> changePassword(@RequestBody @Valid ChangePasswordData changePasswordData) {
        return ResponseUtil.toResponseEntity(userPort.changeUserPassword(changePasswordData), HttpStatus.OK);
    }
}
