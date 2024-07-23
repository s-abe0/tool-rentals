package com.pos.tool_rentals.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pos.toolrentals.entity.Price;
import com.pos.toolrentals.entity.Tool;
import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.repository.ToolRentalsRepository;
import com.pos.toolrentals.service.ToolRentalsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ToolRentalsServiceTest {
	
	@InjectMocks
	private ToolRentalsServiceImpl service;
	
	@Mock
	private ToolRentalsRepository repository;
	
	private static Tool ladw;
	private static Tool jakr;
	private static Tool chns;
	private static Tool jakd;
	
	@BeforeAll
	static void init() {
		Price ladderPrice = Price.builder()
				.dailyCharge(1.99)
				.weekdayCharge(true)
				.weekendCharge(true)
				.holidayCharge(false)
				.build();
		
		Price jackhammerPrice = Price.builder()
				.dailyCharge(2.99)
				.weekdayCharge(true)
				.weekendCharge(false)
				.holidayCharge(false)
				.build();
		
		Price chainsawPrice = Price.builder()
				.dailyCharge(1.49)
				.weekdayCharge(true)
				.weekendCharge(false)
				.holidayCharge(true)
				.build();
		
		ladw = new Tool();
		ladw.setPrice(ladderPrice);
		
		jakr = new Tool();
		jakr.setPrice(jackhammerPrice);
		
		chns = new Tool();
		chns.setPrice(chainsawPrice);
		
		jakd = new Tool();
		jakd.setPrice(jackhammerPrice);
	}
	
	public void testCheckout() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2020);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.DATE, 2);
		LocalDate localDate = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toLocalDate();
		
		System.out.println(calendar.getTime());
		
		Price price = new Price();
		price.setDailyCharge(1.49);
		price.setHolidayCharge(true);
		price.setWeekdayCharge(false);
		price.setWeekendCharge(true);

		Tool tool = new Tool();
		tool.setPrice(price);
		tool.setToolCode("abcd");
		tool.setBrand("brand");
		
		when(repository.findByToolCode(any())).thenReturn(Optional.of(tool));
		
		RentalAgreement agreement = service.checkout("CHNS", 3, localDate, 10);
		
		System.out.println(agreement.getFinalCharge());
	}
	
	@Test
	void testCheckout_scenario1() throws Exception {
		when(repository.findByToolCode(any())).thenReturn(Optional.of(ladw));
		
		RentalAgreement agreement = service.checkout("LADW", 3, LocalDate.of(2020, 7, 2), 10);
		
		assert(agreement.getPreDiscountCharge()).equals(3.98);
		assert(agreement.getDiscountAmount()).equals(0.40);
		assert(agreement.getChargeDays()).equals(2);
		assert(agreement.getFinalCharge()).equals(3.58);
	}
	
	@Test
	void testCheckout_scenario2() throws Exception {
		when(repository.findByToolCode(any())).thenReturn(Optional.of(chns));
		
		RentalAgreement agreement = service.checkout("CHNS", 5, LocalDate.of(2015, 7, 2), 25);

		assert(agreement.getPreDiscountCharge()).equals(4.47);
		assert(agreement.getDiscountAmount()).equals(1.12);
		assert(agreement.getChargeDays()).equals(3);
		assert(agreement.getFinalCharge()).equals(3.35);
	}
	
	@Test
	void testCheckout_scenario3() throws Exception {
		when(repository.findByToolCode(any())).thenReturn(Optional.of(jakd));
		
		RentalAgreement agreement = service.checkout("JAKD", 6, LocalDate.of(2015, 9, 3), 0);

		assert(agreement.getPreDiscountCharge()).equals(8.97);
		assert(agreement.getDiscountAmount()).equals(0.0);
		assert(agreement.getChargeDays()).equals(3);
		assert(agreement.getFinalCharge()).equals(8.97);
	}
	
	@Test
	void testCheckout_scenario4() throws Exception {
		when(repository.findByToolCode(any())).thenReturn(Optional.of(jakr));
		
		RentalAgreement agreement = service.checkout("JAKR", 9, LocalDate.of(2015, 7, 2), 0);

		assert(agreement.getPreDiscountCharge()).equals(14.95);
		assert(agreement.getDiscountAmount()).equals(0.0);
		assert(agreement.getChargeDays()).equals(5);
		assert(agreement.getFinalCharge()).equals(14.95);
	}
	
	@Test
	void testCheckout_scenario5() throws Exception {
		when(repository.findByToolCode(any())).thenReturn(Optional.of(jakr));
		
		RentalAgreement agreement = service.checkout("JAKR", 4, LocalDate.of(2020, 7, 2), 50);

		assert(agreement.getPreDiscountCharge()).equals(2.99);
		assert(agreement.getDiscountAmount()).equals(1.50);
		assert(agreement.getChargeDays()).equals(1);
		assert(agreement.getFinalCharge()).equals(1.50);
	}
}
