package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponse;
import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponseUtil;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.exception.activation.UserToResendActiveAccountCodeNotExistsException;
import com.example.usersservices_mychatserver.exception.auth.AuthServiceException;
import com.example.usersservices_mychatserver.exception.password_reset.UserAccountIsNotActivatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class PasswordResetControllerExceptionHandler extends ResponseStatusExceptionHandler {

    @ExceptionHandler(UserAccountIsNotActivatedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserAccountInNotActivatedException(UserAccountIsNotActivatedException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserToResendActiveAccountCodeNotExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserToResendActiveAccountCodeNotExistsException(UserToResendActiveAccountCodeNotExistsException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.BAD_REQUEST);
    }
}
