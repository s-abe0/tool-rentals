package com.pos.tool_rentals.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.pos.toolrentals.ToolRentalsApplication;
import com.pos.toolrentals.constants.Constants;
import com.pos.toolrentals.controller.ToolRentalsController;
import com.pos.toolrentals.entity.Price;
import com.pos.toolrentals.entity.Tool;
import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.service.ToolRentalsService;

@WebMvcTest(ToolRentalsController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = ToolRentalsApplication.class)
class ToolRentalsControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ToolRentalsService service;
	
	@Test
	void testSuccess() throws Exception {
		// No need for anything specific here
		Price price = Price.builder()
				.dailyCharge(0.0)
				.weekdayCharge(true)
				.weekendCharge(true)
				.holidayCharge(false)
				.build();
		
		Tool tool = new Tool();
		tool.setPrice(price);
		
		RentalAgreement agreement = RentalAgreement.builder()
				.tool(tool)
				.finalCharge(0.0)
				.checkoutDate(LocalDate.now())
				.discountAmount(0.0)
				.discountPercent(0)
				.dueDate(LocalDate.now())
				.preDiscountCharge(0.0)
				.rentalDays(0)
				.build();
		
		String toolCode = "LADW";
		String rentalDays = "3";
		String discount = "10";
		String checkoutDate = "20200702";
		
		when(service.checkout(anyString(), anyInt(), any(LocalDate.class), anyInt())).thenReturn(agreement);
		
		ResultActions result = mockMvc.perform(
				get("/toolRentals/checkout")
					.param("toolCode", toolCode)
					.param("rentalDays", rentalDays)
					.param("discount", discount)
					.param("checkoutDate", checkoutDate.toString()));
		
		result.andExpect(status().isOk());
	}
	
	@Test
	void testInvalidToolCode() throws Exception {
		String toolCode = "LAD";
		String rentalDays = "3";
		String discount = "10";
		String checkoutDate = "20200702";
		
		when(service.checkout(anyString(), anyInt(), any(LocalDate.class), anyInt())).thenReturn(new RentalAgreement());
		
		ResultActions result = mockMvc.perform(
				get("/toolRentals/checkout")
					.param("toolCode", toolCode)
					.param("rentalDays", rentalDays)
					.param("discount", discount)
					.param("checkoutDate", checkoutDate.toString()));
		
		result.andExpect(status().isBadRequest()).andExpect(content().string(containsString(Constants.INVALID_TOOL_CODE_ERROR)));
	}

	@Test
	void testInvalidRentalDays() throws Exception {
		String toolCode = "LADW";
		String rentalDays = "-2";
		String discount = "10";
		String checkoutDate = "20200702";
		
		when(service.checkout(anyString(), anyInt(), any(LocalDate.class), anyInt())).thenReturn(new RentalAgreement());
		
		ResultActions result = mockMvc.perform(
				get("/toolRentals/checkout")
					.param("toolCode", toolCode)
					.param("rentalDays", rentalDays)
					.param("discount", discount)
					.param("checkoutDate", checkoutDate.toString()));
		
		result.andExpect(status().isBadRequest()).andExpect(content().string(containsString(Constants.RENTAL_DAY_MIN_ERROR)));
	}
	
	@Test
	void testInvalidDiscount_min() throws Exception {
		String toolCode = "LADW";
		String rentalDays = "3";
		String discount = "-10";
		String checkoutDate = "20200702";
		
		when(service.checkout(anyString(), anyInt(), any(LocalDate.class), anyInt())).thenReturn(new RentalAgreement());
		
		ResultActions result = mockMvc.perform(
				get("/toolRentals/checkout")
					.param("toolCode", toolCode)
					.param("rentalDays", rentalDays)
					.param("discount", discount)
					.param("checkoutDate", checkoutDate.toString()));
		
		result.andExpect(status().isBadRequest()).andExpect(content().string(containsString(Constants.DISCOUNT_NEGATIVE_ERROR)));
	}
	
	@Test
	void testInvalidDiscount_max() throws Exception {
		String toolCode = "LADW";
		String rentalDays = "3";
		String discount = "1000";
		String checkoutDate = "20200702";
		
		when(service.checkout(anyString(), anyInt(), any(LocalDate.class), anyInt())).thenReturn(new RentalAgreement());
		
		ResultActions result = mockMvc.perform(
				get("/toolRentals/checkout")
					.param("toolCode", toolCode)
					.param("rentalDays", rentalDays)
					.param("discount", discount)
					.param("checkoutDate", checkoutDate.toString()));
		
		result.andExpect(status().isBadRequest()).andExpect(content().string(containsString(Constants.DISCOUNT_TOO_LARGE_ERROR)));
	}
	
	@Test
	void testInvalidDiscount_dateParseFailure() throws Exception {
		String toolCode = "LADW";
		String rentalDays = "3";
		String discount = "10";
		String checkoutDate = "20209999";
		
		when(service.checkout(anyString(), anyInt(), any(LocalDate.class), anyInt())).thenReturn(new RentalAgreement());
		
		ResultActions result = mockMvc.perform(
				get("/toolRentals/checkout")
					.param("toolCode", toolCode)
					.param("rentalDays", rentalDays)
					.param("discount", discount)
					.param("checkoutDate", checkoutDate.toString()));
		
		result.andExpect(status().isBadRequest()).andExpect(content().string(containsString(Constants.INVALID_CHECKOUT_DATE_ERROR)));
	}
	
	@Test
	void testInvalidDiscount_invalidDate() throws Exception {
		String toolCode = "LADW";
		String rentalDays = "3";
		String discount = "10";
		String checkoutDate = "202507";
		
		when(service.checkout(anyString(), anyInt(), any(LocalDate.class), anyInt())).thenReturn(new RentalAgreement());
		
		ResultActions result = mockMvc.perform(
				get("/toolRentals/checkout")
					.param("toolCode", toolCode)
					.param("rentalDays", rentalDays)
					.param("discount", discount)
					.param("checkoutDate", checkoutDate.toString()));
		
		result.andExpect(status().isBadRequest()).andExpect(content().string(containsString(Constants.INVALID_CHECKOUT_DATE_ERROR)));
	}
}
