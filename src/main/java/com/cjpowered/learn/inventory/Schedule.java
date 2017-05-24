package com.cjpowered.learn.inventory;

import java.time.LocalDate;

public interface Schedule {

	boolean canOrderToday(LocalDate today);
	
}
