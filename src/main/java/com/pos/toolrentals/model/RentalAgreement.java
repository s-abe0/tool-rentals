package com.pos.toolrentals.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pos.toolrentals.entity.Price;
import com.pos.toolrentals.entity.Tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RentalAgreement {
	private Tool tool;
	private Integer rentalDays;
	private LocalDate checkoutDate;
	private LocalDate dueDate;
	private Integer chargeDays;
	private Double preDiscountCharge;
	private Integer discountPercent;
	private Double discountAmount;
	private Double finalCharge;
	
	public String toString() {
		Price price = this.tool.getPrice();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
		String currencyFormat = "$%(,.2f";
		StringBuilder sb = new StringBuilder();
		
		sb.append("Tool code: ").append(this.getTool().getToolCode())
			.append("\nTool type: ").append(this.getTool().getToolType())
			.append("\nTool brand: ").append(this.getTool().getBrand())
			.append("\nRental days: ").append(this.getRentalDays())
			.append("\nCheckout date: ")
			.append(dateFormatter.format(this.getCheckoutDate()))
			.append("\nDue date: ")
			.append(dateFormatter.format(this.getDueDate()))
			.append("\nDaily rental charge: ")
			.append(String.format(currencyFormat, price.getDailyCharge()))
			.append("\nCharge days: ").append(this.getChargeDays())
			.append("\nPre-discount charge: ")
			.append(String.format(currencyFormat, this.getPreDiscountCharge()))
			.append("\nDiscount percent: ").append(this.getDiscountPercent()).append("%")
			.append("\nDiscount amount: ")
			.append(String.format(currencyFormat, this.getDiscountAmount()))
			.append("\nFinal charge: ")
			.append(String.format(currencyFormat, this.getFinalCharge()));
			
		return sb.toString();
	}
}
