package com.pos.toolrentals.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse implements ToolRentalsResponse {
	private String errorMsg;
}
