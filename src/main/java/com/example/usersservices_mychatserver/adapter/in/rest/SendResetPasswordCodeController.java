package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.entity.UserEmailAndCode;
import com.example.usersservices_mychatserver.entity.UserEmailData;
import com.example.usersservices_mychatserver.port.in.CheckIsCorrectResetPasswordCodeUseCase;
import com.example.usersservices_mychatserver.port.in.SendResetPasswordCodeUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/send-reset-password-code")
public class SendResetPasswordCodeController {

    final private SendResetPasswordCodeUseCase sendResetPasswordCodeUseCase;
    final private CheckIsCorrectResetPasswordCodeUseCase checkIsCorrectResetPasswordCodeUseCase;
    private final ObjectMapper objectMapper ;
    public SendResetPasswordCodeController(SendResetPasswordCodeUseCase sendResetPasswordCodeUseCase, CheckIsCorrectResetPasswordCodeUseCase checkIsCorrectResetPasswordCodeUseCase, ObjectMapper objectMapper) {
        this.sendResetPasswordCodeUseCase = sendResetPasswordCodeUseCase;
        this.checkIsCorrectResetPasswordCodeUseCase = checkIsCorrectResetPasswordCodeUseCase;
        this.objectMapper = objectMapper;
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> sendResetPasswordCode(@RequestBody @Valid Mono<UserEmailData> user) {

        return sendResetPasswordCodeUseCase.sendResetPasswordCode(user).flatMap(isOk->
        {
            if(isOk.isError()){
                return Mono.just(ResponseEntity.badRequest().body(isOk.getError()));
            }else{
                try {
                    String isOkJSON  = objectMapper.writeValueAsString(isOk.getValue());
                    return Mono.just(ResponseEntity.ok(isOkJSON));
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error"));
                }

            }
        });

        }
    @PostMapping("/isCorrectCode")
    public Mono<ResponseEntity<String>> isCorrectResetPasswordCode(@RequestBody @Valid Mono<UserEmailAndCode> userEmailAndCodeMono) {

        return checkIsCorrectResetPasswordCodeUseCase.checkIsCorrectResetPasswordCode(userEmailAndCodeMono).flatMap(isCorrectCode->
        {
            if(isCorrectCode.isError()){
                return Mono.just(ResponseEntity.badRequest().body(isCorrectCode.getError()));
            }else{
                try {
                    String isOkJSON  = objectMapper.writeValueAsString(isCorrectCode.getValue());
                    return Mono.just(ResponseEntity.ok(isOkJSON));
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error"));
                }

            }
        });

    }



}
