package com.pos.toolrentals.controller;

import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pos.toolrentals.constants.Constants;
import com.pos.toolrentals.model.ErrorResponse;
import com.pos.toolrentals.model.ToolNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * An exception handler for the ToolRentalsController to handle request parameter validation errors, tool not found errors, 
 * and any other generic errors that may occur.
 * 
 * @see ToolRentalsController
 * @see ToolNotFoundException
 * @see ErrorResponse
 */
@RestControllerAdvice(assignableTypes = { ToolRentalsController.class })
public class ToolRentalsControllerExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ToolRentalsControllerExceptionHandler.class);
	
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ResponseEntity<ErrorResponse> handleConstraintValidationException(HttpServletRequest req, Exception ex) {
		LOGGER.error(ex.getMessage(), ex);
		
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	// Handles parsing the checkoutDate
	@ExceptionHandler(value = { DateTimeParseException.class })
	public ResponseEntity<ErrorResponse> handleMethodArgumentMismatchException(HttpServletRequest req, Exception ex) {
		LOGGER.error(ex.getMessage(), ex);
		
		return new ResponseEntity<>(new ErrorResponse(Constants.INVALID_CHECKOUT_DATE_ERROR), HttpStatus.BAD_REQUEST);
	}

	
	@ExceptionHandler(value = { ToolNotFoundException.class })
	public ResponseEntity<ErrorResponse> handleToolNotFoundException(HttpServletRequest req, Exception ex) {
		LOGGER.info(ex.getMessage());
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericError(HttpServletRequest req, Exception ex) {
		LOGGER.error(ex.getMessage(), ex);
		
		return new ResponseEntity<>(new ErrorResponse("Service Failure"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
