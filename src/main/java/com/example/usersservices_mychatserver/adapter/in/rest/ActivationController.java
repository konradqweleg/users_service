package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ConvertToJSON;
import com.example.usersservices_mychatserver.adapter.in.rest.util.ResponseUtil;
import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.UserEmailData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/users/activate")
public class ActivationController {
    private final UserPort userPort;

    public ActivationController(UserPort userPort) {
        this.userPort = userPort;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> activeUserAccount(@RequestBody @Valid ActiveAccountCodeData codeVerificationMono) {
        return ResponseUtil.toResponseEntity(userPort.activateUserAccount(codeVerificationMono), HttpStatus.OK);
    }

    @PostMapping("/resend-activation-code")
    public Mono<ResponseEntity<String>> resendActiveUserAccountCode(@RequestBody @Valid Mono<UserEmailData> emailDataMono) {
        return userPort.resendActiveUserAccountCode(emailDataMono).flatMap(ConvertToJSON::convert);
    }
}
