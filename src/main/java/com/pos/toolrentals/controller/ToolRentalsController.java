package com.pos.toolrentals.controller;

import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pos.toolrentals.constants.Constants;
import com.pos.toolrentals.entity.Tool;
import com.pos.toolrentals.model.CheckoutResponse;
import com.pos.toolrentals.model.ErrorResponse;
import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.model.ToolRentalsResponse;
import com.pos.toolrentals.service.ToolsService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Controller
@Validated
@RequestMapping("/toolRentals")
public class ToolRentalsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ToolRentalsController.class);
	private final String DATE_FORMAT = "yyyyMMdd";
	
	@Autowired
	private ToolsService service;
	
	@GetMapping("/getTool")
	public ResponseEntity<Tool> getTool(@RequestParam String toolCode) {
		LOGGER.info("Getting tool with code {}", toolCode);
		
		Optional<Tool> tool = service.getTool(toolCode);	
		if(tool.isPresent()) {
			return new ResponseEntity<>(tool.get(), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@GetMapping("/checkout")
	public ResponseEntity<ToolRentalsResponse> checkout(
			@RequestParam
				@Pattern(regexp="\\w{4}", message=Constants.INVALID_TOOL_CODE_ERROR) 
				String toolCode,
			@RequestParam 
				@Min(value=1, message=Constants.RENTAL_DAY_MIN_ERROR) 
				int rentalDays,
			@RequestParam 
				@Min(value=0, message=Constants.DISCOUNT_NEGATIVE_ERROR) 
				@Max(value=100, message=Constants.DISCOUNT_TOO_LARGE_ERROR) 
				int discount,
			@RequestParam 
				@DateTimeFormat(pattern = DATE_FORMAT)
				LocalDate checkoutDate) throws Exception {
		
		LOGGER.info("Checking out {} for {} days, beginning {} with a {} percent discount", toolCode, rentalDays, checkoutDate, discount);
		
		if(LocalDate.now().isBefore(checkoutDate)) {
			LOGGER.error("checkoutDate not valid");
			
			ErrorResponse error = new ErrorResponse(Constants.INVALID_CHECKOUT_DATE_ERROR);
			
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		
		RentalAgreement agreement = service.checkout(toolCode, rentalDays, checkoutDate, discount);
		
		return new ResponseEntity<>(new CheckoutResponse(agreement), HttpStatus.OK);
	}
}
