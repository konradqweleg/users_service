package com.example.usersservices_mychatserver.adapter.in.rest;


import com.example.usersservices_mychatserver.adapter.in.rest.util.PrepareResultPort;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.in.ActiveUserAccountPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/activeAccount")
public class ActiveUserAccountController {

    private final ActiveUserAccountPort activeUserAccountPort;

    private final PrepareResultPort convertObjectToJsonResponse;

    public ActiveUserAccountController(ActiveUserAccountPort activeUserAccountPort, PrepareResultPort convertObjectToJsonResponse) {
        this.activeUserAccountPort = activeUserAccountPort;
        this.convertObjectToJsonResponse = convertObjectToJsonResponse;
    }

    @PostMapping("/resendCode")
    public Mono<ResponseEntity<String>> resendActiveUserAccountCode(@RequestBody Mono<IdUserData> idUserDataMono) {
        return activeUserAccountPort.resendActiveUserAccountCode(idUserDataMono).flatMap(convertObjectToJsonResponse::convert);
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> activeUserAccount(@RequestBody Mono<CodeVerification> codeVerificationMono) {
        return activeUserAccountPort.activateUserAccount(codeVerificationMono).flatMap(convertObjectToJsonResponse::convert);
    }
}
