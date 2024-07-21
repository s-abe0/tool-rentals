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
import com.pos.toolrentals.repository.ToolsRepository;

@Service
public class ToolsServiceImpl implements ToolsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ToolsServiceImpl.class);

	@Autowired
	private ToolsRepository repository;
	
	@Override
	public Optional<Tool> getTool(String toolCode) {
		LOGGER.info("Retrieving tool with code {}", toolCode);
		
		return repository.findByToolCode(toolCode);
	}

	@Override
	public Iterable<Tool> getAllTools() {
		LOGGER.info("Retrieving all tools from repository");
		return repository.findAll();
	}

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
		
		agreement.setTool(tool);
		agreement.setRentalDays(rentalDays);
		agreement.setCheckoutDate(checkoutDate);
		agreement.setDueDate(checkoutDate.plusDays(rentalDays));
		agreement.setFinalCharge(calculateTotalCost(tool, rentalDays, checkoutDate, discount));
		
		return agreement;
	}
	
	private double calculateTotalCost(Tool tool, int rentalDays, LocalDate checkoutDate, int discount) {
		Price price = tool.getPrice();
		double totalCost = 0.0;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(
				Date.from(checkoutDate
						.atStartOfDay()
						.atZone(ZoneId.systemDefault())
						.toInstant()));
		
		for(int i = 0; i < rentalDays; i++) {
			calendar.add(Calendar.DATE, 1);

			if(isWeekend(calendar)) {
				if(price.getWeekendCharge()) {
					totalCost += price.getDailyCharge();
				}
				
				continue;
			}
			
			else if(isHoliday(calendar)) {
				if(price.getHolidayCharge()) {
					totalCost += price.getDailyCharge();
				}
				
				continue;
			}
			
			else if(price.getWeekdayCharge()) {
				totalCost += price.getDailyCharge();
			}
		}
		
		return totalCost;
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
}
