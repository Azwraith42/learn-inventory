package com.cjpowered.learn.inventory;

import java.time.LocalDate;

public class FirstOfTheMonth implements Schedule {

	@Override
	public boolean canOrderToday(LocalDate today) {
		return  today.getDayOfMonth() == 1;
	}

}
