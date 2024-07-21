package com.pos.toolrentals.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pos.toolrentals.entity.Price;
import com.pos.toolrentals.entity.Tool;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RentalAgreement {
	private Tool tool;
	private Integer rentalDays;
	private LocalDate checkoutDate;
	private LocalDate dueDate;
	private Price price;
	private Integer chargeDays;
	private Double preDiscountCharge;
	private Double discountPercent;
	private Double discountAmount;
	private Double finalCharge;
}
