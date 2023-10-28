package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.PrepareResultPort;
import com.example.usersservices_mychatserver.entity.request.UserEmailAndCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.port.in.ResetPasswordCodePort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/resetPasswordCode")
public class SendResetPasswordCodeController {

    private final ResetPasswordCodePort resetPasswordCodePort;
    private final PrepareResultPort convertObjectToJsonResponse;

    public SendResetPasswordCodeController(ResetPasswordCodePort resetPasswordCodePort, PrepareResultPort convertObjectToJsonResponse) {
        this.resetPasswordCodePort = resetPasswordCodePort;
        this.convertObjectToJsonResponse = convertObjectToJsonResponse;
    }

    @PostMapping("/sendCode")
    public Mono<ResponseEntity<String>> sendResetPasswordCode(@RequestBody @Valid Mono<UserEmailData> user) {
        return resetPasswordCodePort.sendResetPasswordCode(user).flatMap(convertObjectToJsonResponse::convert);
    }

    @PostMapping("/checkCode")
    public Mono<ResponseEntity<String>> isCorrectResetPasswordCode(@RequestBody @Valid Mono<UserEmailAndCodeData> userEmailAndCodeMono) {
        return resetPasswordCodePort.checkIsCorrectResetPasswordCode(userEmailAndCodeMono).flatMap(convertObjectToJsonResponse::convert);
    }


}
