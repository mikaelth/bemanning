package se.uu.ebc.bemanning.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.OptimisticLockingFailureException;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
/* 

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        if (e instanceof ArithmeticException) {
            return "params error";
        }
        if (e instanceof Exception) {
            return "Internal server exception";
        }
        return null;
    }

 */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
		log.debug("ResourceNotFoundException caught " + ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
    		ConfigurationException.class, MappingException.class, UnsupportedOperationException.class,
    		ClassCastException.class, NullPointerException.class, OptimisticLockingFailureException.class
    	})
    public ResponseEntity<String> handleInternalExceptions(Exception ex) {
		log.debug("Internal error exception caught " + ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
    		IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class
    	})
    public ResponseEntity<String> handleBadRequestExceptions(Exception ex) {
		log.debug("Bad request exception caught " + ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
		log.debug("General exception caught " + ex);
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}