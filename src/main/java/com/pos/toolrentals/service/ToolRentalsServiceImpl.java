package com.pos.toolrentals.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.toolrentals.entity.Price;
import com.pos.toolrentals.entity.Tool;
import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.model.ToolNotFoundException;
import com.pos.toolrentals.repository.ToolRentalsRepository;

/**
 * This service handles the checkout process business logic for the Tool Rentals service.
 * It handles calculation of the total charge days, discount amount, final charge amount, etc.
 */
@Service
public class ToolRentalsServiceImpl implements ToolRentalsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ToolRentalsServiceImpl.class);

	@Autowired
	private ToolRentalsRepository repository;

	/**
	 * Checkout a tool and generate a rental agreement
	 * 
	 * @param toolCode The unique identifier for the tool being checked out
	 * @param rentalDays The total amount of days the tool will be checked out
	 * @param checkoutDate The date of checkout
	 * @param discount The discount to be subtracted from the total cost
	 * @throws ToolNotFoundException If the specified tool via toolCode lookup is not found within the database
	 * @return RentalAgreement A rental agreement outlining the details of the tool rental, including the final charge.
	 */
	@Override
	public RentalAgreement checkout(String toolCode, int rentalDays, LocalDate checkoutDate, int discount) throws ToolNotFoundException {
		Optional<Tool> tool = repository.findByToolCode(toolCode);
		
		// If the requested tool is not found in the database, trigger the exception handler to return a 204 No Content response
		if(tool.isEmpty()) {
			throw new ToolNotFoundException("Tool with code " + toolCode + " not found");
		}
		
		return generateRentalAgreement(tool.get(), rentalDays, checkoutDate, discount);
	}
	
	private RentalAgreement generateRentalAgreement(Tool tool, int rentalDays, LocalDate checkoutDate, int discount) {
		RentalAgreement agreement = new RentalAgreement();
		Price price = tool.getPrice();
		int chargeDays = calculateChargeDays(price, rentalDays, checkoutDate);
		
		double preDiscountCharge = chargeDays * price.getDailyCharge();
		double discountAmount = (discount / 100.0) * preDiscountCharge;
		double finalCharge = preDiscountCharge - discountAmount;
		
		agreement.setTool(tool);
		agreement.setRentalDays(rentalDays);
		agreement.setCheckoutDate(checkoutDate);
		agreement.setDueDate(checkoutDate.plusDays(rentalDays));
		agreement.setChargeDays(calculateChargeDays(price, rentalDays, checkoutDate));
		agreement.setPreDiscountCharge(roundToCents(preDiscountCharge));
		agreement.setDiscountPercent(discount);
		agreement.setDiscountAmount(roundToCents(discountAmount));
		agreement.setFinalCharge(roundToCents(finalCharge));
		
		return agreement;
	}
	
	private int calculateChargeDays(Price price, int rentalDays, LocalDate checkoutDate) {
		int chargeDays = 0;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(
				Date.from(checkoutDate
						.atStartOfDay()
						.atZone(ZoneId.systemDefault())
						.toInstant()));
		
		// Iterate through the rental days, calculating which day is a charge day and adding it to the total sum
		for(int i = 0; i < rentalDays; i++) {
			calendar.add(Calendar.DATE, 1);

			if(isWeekend(calendar)) {
				if(price.getWeekendCharge()) {
					chargeDays++;
				}
				
				continue;
			}
			
			else if(isHoliday(calendar)) {
				if(price.getHolidayCharge()) {
					chargeDays++;
				}
				
				continue;
			}
			
			// If not a weekend or holiday, it has to be a weekday
			else if(price.getWeekdayCharge()) {
				chargeDays++;
			}
		}
				
		return chargeDays;
	}
	
	private boolean isWeekend(Calendar c) {
		return c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}
	
	private boolean isHoliday(Calendar currentDate) {
		// Check for labor day
		if(currentDate.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
			
			// Calculate labor day date within currentDate year
			Calendar laborDay = Calendar.getInstance();
			laborDay.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
			laborDay.set(Calendar.MONTH, Calendar.SEPTEMBER);
			laborDay.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
			laborDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			
			return currentDate.get(Calendar.DAY_OF_MONTH) == laborDay.get(Calendar.DAY_OF_MONTH);
		}
		
		// Check for independence day
		if(currentDate.get(Calendar.MONTH) == Calendar.JULY) {
			
			// If today is Friday July 3, Independence day is observed then
			if(currentDate.get(Calendar.DAY_OF_MONTH) == 3 && currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
				return true;
			}
			
			// If today is Monday July 5, Independence day is observed then
			if(currentDate.get(Calendar.DAY_OF_MONTH) == 5 && currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				return true;
			}
			
			if(currentDate.get(Calendar.DAY_OF_MONTH) == 4) {
				return true;
			}
		}
		
		return false;
	}
	
	// Calculate total charge to the nearest 2 decimal places (cents)
	private double roundToCents(double amount) {
		LOGGER.debug("Rounding {} to the nearest cents", amount);
		return Math.round(amount * 100.0) / 100.0;
	}
}
