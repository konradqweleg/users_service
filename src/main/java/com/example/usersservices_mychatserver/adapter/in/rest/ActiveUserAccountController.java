package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.model.ActiveUserAccountData;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.in.ActivateUserAccountUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/activeAccount")
public class ActiveUserAccountController {
    private final ActivateUserAccountUseCase activateUserAccountUseCase;

    public ActiveUserAccountController(ActivateUserAccountUseCase activateUserAccountUseCase) {
        this.activateUserAccountUseCase = activateUserAccountUseCase;
    }

    @PostMapping("")
    public Mono<ActiveUserAccountData> activeUserAccount(@RequestBody Mono<CodeVerification> codeVerificationMono) {
        return activateUserAccountUseCase.activateUserAccount(codeVerificationMono).map(
                ActiveUserAccountData::new);
    }
}
