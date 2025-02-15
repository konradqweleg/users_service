package com.example.usersservices_mychatserver.adapter.in.rest.exception_handlers;


import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponse;
import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponseUtil;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.exception.auth.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class AuthControllerExceptionHandler extends ResponseStatusExceptionHandler {
    @ExceptionHandler(AuthServiceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserAlreadyRegisteredException(AuthServiceException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(SendVerificationCodeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSendVerificationCodeException(SendVerificationCodeException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnauthorizedException(UnauthorizedException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.UNAUTHORIZED);
    }

}