package com.pos.toolrentals.service;

import java.time.LocalDate;

import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.model.ToolNotFoundException;

public interface ToolRentalsService {
	public RentalAgreement checkout(String toolCode, int rentalDays, LocalDate checkoutDate, int discount) throws ToolNotFoundException;
}
