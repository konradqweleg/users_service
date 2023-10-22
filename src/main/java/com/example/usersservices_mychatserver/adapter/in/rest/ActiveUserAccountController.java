package com.example.usersservices_mychatserver.adapter.in.rest;


import com.example.usersservices_mychatserver.adapter.in.rest.util.PrepareResultPort;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.in.ActivateUserAccountUseCase;
import com.example.usersservices_mychatserver.port.in.ResendActiveUserAccountCodeUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/activeAccount")
public class ActiveUserAccountController {
    private final ActivateUserAccountUseCase activateUserAccountUseCase;
    private final ResendActiveUserAccountCodeUseCase resendActiveUserAccountCodeUseCase;
    private final PrepareResultPort convertObjectToJsonResponse;

    public ActiveUserAccountController(ActivateUserAccountUseCase activateUserAccountUseCase, ResendActiveUserAccountCodeUseCase resendActiveUserAccountCodeUseCase, PrepareResultPort convertObjectToJsonResponse) {
        this.activateUserAccountUseCase = activateUserAccountUseCase;
        this.resendActiveUserAccountCodeUseCase = resendActiveUserAccountCodeUseCase;
        this.convertObjectToJsonResponse = convertObjectToJsonResponse;
    }

    @PostMapping("/resendCode")
    public Mono<ResponseEntity<String>> resendActiveUserAccountCode(@RequestBody Mono<IdUserData> idUserDataMono) {
        return resendActiveUserAccountCodeUseCase.resendActiveUserAccountCode(idUserDataMono).flatMap(convertObjectToJsonResponse::convert);
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> activeUserAccount(@RequestBody Mono<CodeVerification> codeVerificationMono) {
        return activateUserAccountUseCase.activateUserAccount(codeVerificationMono).flatMap(convertObjectToJsonResponse::convert);
    }
}
