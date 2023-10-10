package com.example.usersservices_mychatserver.adapter.in.rest;


import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.in.ActivateUserAccountUseCase;
import com.example.usersservices_mychatserver.port.in.ResendActiveUserAccountCodeUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/activeAccount")
public class ActiveUserAccountController {
    private final ActivateUserAccountUseCase activateUserAccountUseCase;
    private final ResendActiveUserAccountCodeUseCase resendActiveUserAccountCodeUseCase;
    private final ObjectMapper objectMapper ;

    public ActiveUserAccountController(ActivateUserAccountUseCase activateUserAccountUseCase, ResendActiveUserAccountCodeUseCase resendActiveUserAccountCodeUseCase, ObjectMapper objectMapper) {
        this.activateUserAccountUseCase = activateUserAccountUseCase;
        this.resendActiveUserAccountCodeUseCase = resendActiveUserAccountCodeUseCase;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/resendCode")
    public Mono<ResponseEntity<String>> resendActiveUserAccountCode(@RequestBody Mono<IdUserData> idUserDataMono) {
        return resendActiveUserAccountCodeUseCase.resendActiveUserAccountCode(idUserDataMono).flatMap(response -> {
                    if (response.isError()) {
                        return Mono.just(ResponseEntity.badRequest().body(response.getError()));
                    } else {
                        try {
                            Status resendCodeResponse = response.getValue();
                            String activeUserAccountDataJSON = objectMapper.writeValueAsString(resendCodeResponse);
                            return Mono.just(ResponseEntity.ok(activeUserAccountDataJSON));
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Error"));
                        }

                    }


                }
        );
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> activeUserAccount(@RequestBody Mono<CodeVerification> codeVerificationMono) {
        return activateUserAccountUseCase.activateUserAccount(codeVerificationMono).flatMap(response -> {
                    if (response.isError()) {
                        return Mono.just(ResponseEntity.badRequest().body(response.getError()));
                    } else {
                        try {
                            Status activateUserAccount = response.getValue();
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
