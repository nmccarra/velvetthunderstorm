package com.nmccarra.velvetthunderstorm.controller.exceptionhandler;

import com.nmccarra.velvetthunderstorm.core.exception.NoMeasurementsFoundException;
import com.nmccarra.velvetthunderstorm.core.exception.NoSensorIdsProvidedException;
import com.nmccarra.velvetthunderstorm.model.ErrorRep;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorRep> handleSensorValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorRep error = new ErrorRep()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .timestamp(LocalDateTime.now().atOffset(java.time.ZoneOffset.UTC))
                .path(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorRep> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ErrorRep error = new ErrorRep()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now().atOffset(java.time.ZoneOffset.UTC))
                .path(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoMeasurementsFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorRep> handleNoMeasurementsFoundException(
            NoMeasurementsFoundException ex,
            HttpServletRequest request
    ) {
        ErrorRep error = new ErrorRep()
                .status(404)
                .errors(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now().atOffset(java.time.ZoneOffset.UTC))
                .path(request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorRep> handleDateTimeParseException(
            DateTimeParseException ex,
            HttpServletRequest request
    ) {
        ErrorRep error = new ErrorRep()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now().atOffset(java.time.ZoneOffset.UTC))
                .path(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorRep> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        ErrorRep error = new ErrorRep()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(List.of("Invalid value for parameter '" + ex.getName() + "': " + ex.getValue()))
                .timestamp(LocalDateTime.now().atOffset(java.time.ZoneOffset.UTC))
                .path(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSensorIdsProvidedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorRep> handleNoSensorIdsProvidedException(
            NoSensorIdsProvidedException ex,
            HttpServletRequest request
    ) {
        ErrorRep error = new ErrorRep()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now().atOffset(java.time.ZoneOffset.UTC))
                .path(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
