package com.example.usersservices_mychatserver.adapter.in.rest;


import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponse;
import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponseUtil;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.exception.UnexpectedError;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
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
    public Mono<ResponseEntity<ErrorResponse>> handleAuthServiceError(AuthServiceException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SaveDataInRepositoryException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSaveDataInRepositoryException(AuthServiceException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnexpectedError.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnexpectedException(AuthServiceException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}