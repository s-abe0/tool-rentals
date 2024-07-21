package com.pos.toolrentals.service;

import java.time.LocalDate;
import java.util.Optional;

import com.pos.toolrentals.entity.Tool;
import com.pos.toolrentals.model.RentalAgreement;
import com.pos.toolrentals.model.ToolNotFoundException;

public interface ToolsService {
	public Optional<Tool> getTool(String toolCode);
	public Iterable<Tool> getAllTools();
	public RentalAgreement checkout(String toolCode, int rentalDays, LocalDate checkoutDate, int discount) throws ToolNotFoundException;
}
