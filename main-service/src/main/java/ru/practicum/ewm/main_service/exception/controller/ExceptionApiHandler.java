package ru.practicum.ewm.main_service.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.ewm.main_service.exception.model.BadRequestException;
import ru.practicum.ewm.main_service.exception.model.ConflictException;
import ru.practicum.ewm.main_service.exception.model.ErrorResponse;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse entityIsAlreadyExist(ConflictException exception) {
        log.warn("Entity is already. Message: {}, StackTrace: {}", exception.getMessage(), exception.getStackTrace());

        return new ErrorResponse(exception.getMessage(), "Entity is already exist!", HttpStatus.CONFLICT.toString());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityIsNotExist(NotFoundException exception) {
        log.warn("Entity is not found. Message: {}, StackTrace: {}", exception.getMessage(), exception.getStackTrace());

        return new ErrorResponse(exception.getMessage(), "Entity is not found!", HttpStatus.NOT_FOUND.toString());
    }



    @ExceptionHandler({MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse commonValidation(MethodArgumentNotValidException e) {
        List<FieldError> items = e.getBindingResult().getFieldErrors();
        String message = items.stream()
                .map(FieldError::getField)
                .findFirst()
                .orElse("Unknown error");
        Optional<String> title = items.stream()
                .map(FieldError::getDefaultMessage)
                .findFirst();
        if (title.isPresent()) {
            message = message + " - " + title.get();
        }
        log.warn(message);

        return new ErrorResponse(message, "Validation error",HttpStatus.BAD_REQUEST.toString());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final Throwable e) {
        log.warn("MissingServletRequestParameterException. Message: {}, StackTrace: {}", e.getMessage(), e.getStackTrace());

        return new ErrorResponse(e.getMessage(), "Validation error", BAD_REQUEST.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(final Throwable e) {
        log.warn("Unknown error. Message: {}, StackTrace: {}", e.getMessage(), e.getStackTrace());

        return new ErrorResponse(e.getMessage(), "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final BadRequestException e) {
        log.warn(e.getMessage());

        return new ErrorResponse(e.getParameter(), "Bad request", HttpStatus.BAD_REQUEST.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException. Message: {}, StackTrace: {}", e.getMessage(), e.getStackTrace());


        return new ErrorResponse(e.getMessage(), "Validation error", HttpStatus.BAD_REQUEST.toString());
    }
}