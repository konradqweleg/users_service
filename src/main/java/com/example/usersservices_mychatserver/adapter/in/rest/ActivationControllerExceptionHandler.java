package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponse;
import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponseUtil;
import com.example.usersservices_mychatserver.exception.activation.ActivationCodeNotFoundException;
import com.example.usersservices_mychatserver.exception.activation.BadActiveAccountCodeException;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ActivationControllerExceptionHandler extends ResponseStatusExceptionHandler {
    @ExceptionHandler(ActivationCodeNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleActivationCodeNotFound(ActivationCodeNotFoundException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadActiveAccountCodeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadActivationCode(BadActiveAccountCodeException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.BAD_REQUEST);
    }
}
