package com.pos.toolrentals.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pos.toolrentals.constants.Constants;
import com.pos.toolrentals.model.CheckoutResponse;
import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.model.ToolRentalsResponse;
import com.pos.toolrentals.service.ToolRentalsService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Controller
@Validated
@RequestMapping("/toolRentals")
public class ToolRentalsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ToolRentalsController.class);
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	@Autowired
	private ToolRentalsService service;
	
	/**
	 * Checks out a tool and generates a RentalAgreement.
	 * 
	 * @param toolCode The unique identifier for the tool being checked out. Must be 4 characters
	 * @param rentalDays The total amount of days the tool will be checked out. Must be minimum of 1
	 * @param discount The discount to be subtracted from the total cost. Must be between 0 and 100
	 * @param checkoutDate The date of checkout. Must be in format yyyyMMdd
	 * @return ToolRentalsResponse Contains a rental agreement outlining the details of the tool rental, including the final charge.
	 * @throws Exception
	 */
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
				@Pattern(regexp="\\d{8}", message=Constants.INVALID_CHECKOUT_DATE_ERROR)
				String checkoutDate) throws Exception {
		
		LOGGER.info("Checking out {} for {} days, beginning {} with a {} percent discount", toolCode, rentalDays, checkoutDate, discount);
		
		LocalDate localCheckoutDate = LocalDate.parse(checkoutDate, dateFormatter);
		RentalAgreement agreement = service.checkout(toolCode, rentalDays, localCheckoutDate, discount);
		
		LOGGER.info("Generated rental agreement:\n{}", agreement.toString());
		
		return new ResponseEntity<>(new CheckoutResponse(agreement), HttpStatus.OK);
	}
}
