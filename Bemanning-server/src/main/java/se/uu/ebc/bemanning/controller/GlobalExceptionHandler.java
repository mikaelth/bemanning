package se.uu.ebc.bemanning.controller;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;

import org.thymeleaf.exceptions.TemplateInputException;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private record ErrorResponse (int statusCode, String message, LocalDateTime timeStamp) {};


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		log.error("ResourceNotFoundException caught " + ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOIssueFound(ResourceNotFoundException ex) {
		log.error("IOException caught " + ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
       return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

   @ExceptionHandler({
    		ConfigurationException.class, MappingException.class, UnsupportedOperationException.class,
    		ClassCastException.class, NullPointerException.class, OptimisticLockingFailureException.class,
    		TemplateInputException.class
    	})
    public ResponseEntity<ErrorResponse> handleInternalExceptions(Exception ex) {
		log.error("Internal error exception caught " + ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
     return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
    		IllegalArgumentException.class,
    		MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    	})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex) {
		log.error("Bad request exception caught " + ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
//		log.error("General exception caught {}", ex);
		log.error("General exception caught: {}, {}", ex.getMessage(), ex);
           ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
