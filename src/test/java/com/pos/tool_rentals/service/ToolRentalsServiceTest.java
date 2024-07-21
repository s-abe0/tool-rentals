package com.pos.tool_rentals.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pos.toolrentals.entity.Price;
import com.pos.toolrentals.entity.Tool;
import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.repository.ToolsRepository;
import com.pos.toolrentals.service.ToolsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ToolRentalsServiceTest {
	
	@InjectMocks
	private ToolsServiceImpl service;
	
	@Mock
	private ToolsRepository repository;
	
	@Test
	public void testCheckout() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		LocalDate localDate = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toLocalDate();
		
		System.out.println(calendar.getTime());
		
		Price price = new Price();
		price.setDailyCharge(1.00);
		price.setHolidayCharge(true);
		price.setWeekdayCharge(false);
		price.setWeekendCharge(true);

		Tool tool = new Tool();
		tool.setPrice(price);
		
		when(repository.findByToolCode(any())).thenReturn(Optional.of(tool));
		
		RentalAgreement agreement = service.checkout("CHNS", 3, localDate, 0);
		
		System.out.println(agreement.getFinalCharge());
	}
}
