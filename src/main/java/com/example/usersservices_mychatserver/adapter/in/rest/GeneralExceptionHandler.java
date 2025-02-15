package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponse;
import com.example.usersservices_mychatserver.adapter.in.rest.error.ErrorResponseUtil;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.exception.UnexpectedInternalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

public class GeneralExceptionHandler extends ResponseStatusExceptionHandler {
    @ExceptionHandler(UnexpectedInternalException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnexpectedException(UnexpectedInternalException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(SaveDataInRepositoryException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSaveDataInRepositoryException(SaveDataInRepositoryException ex, ServerWebExchange exchange) {
        return ErrorResponseUtil.generateErrorResponseEntity(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
