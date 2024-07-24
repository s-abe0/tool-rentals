package com.pos.toolrentals.model;

public class ToolNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ToolNotFoundException(String errorMsg) {
		super(errorMsg);
	}
}
