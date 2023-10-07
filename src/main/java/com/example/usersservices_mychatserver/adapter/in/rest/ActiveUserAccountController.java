package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.entity.ActiveUserAccountData;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.in.ActivateUserAccountUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/activeAccount")
public class ActiveUserAccountController {
    private final ActivateUserAccountUseCase activateUserAccountUseCase;
    private final ObjectMapper objectMapper ;

    public ActiveUserAccountController(ActivateUserAccountUseCase activateUserAccountUseCase, ObjectMapper objectMapper) {
        this.activateUserAccountUseCase = activateUserAccountUseCase;
        this.objectMapper = objectMapper;
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> activeUserAccount(@RequestBody Mono<CodeVerification> codeVerificationMono) {
        return activateUserAccountUseCase.activateUserAccount(codeVerificationMono).flatMap(response -> {
                    if (response.isError()) {
                        return Mono.just(ResponseEntity.badRequest().body(response.getError()));
                    } else {
                        try {
                            ActiveUserAccountData activateUserAccount = response.getValue();
                            String activeUserAccountDataJSON = objectMapper.writeValueAsString(activateUserAccount);
                            return Mono.just(ResponseEntity.ok(activeUserAccountDataJSON));
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Error"));
                        }

                    }


                }
        );
    }
}
