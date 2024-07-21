package com.pos.toolrentals.model;

public class ToolNotFoundException extends Exception {
	public ToolNotFoundException(String errorMsg) {
		super(errorMsg);
	}
}
