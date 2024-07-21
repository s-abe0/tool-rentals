package com.pos.tool_rentals;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


class ToolRentalsApplicationTests {

	@Test
	public void calendarTest() {
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.YEAR, 2022);
		c.set(Calendar.MONTH, Calendar.SEPTEMBER);
		c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		

		
		System.out.println(c.getTime());
		
		Calendar c2 = Calendar.getInstance();
		
		c2.set(Calendar.MONTH, Calendar.SEPTEMBER);
		c2.set(Calendar.DAY_OF_MONTH, 2);
		c2.set(Calendar.YEAR, 2020);
		
		System.out.println(c2.getTime());
		
		System.out.println(c.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
	}
	
	private boolean isWeekend(Calendar c) {
		return c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

}
