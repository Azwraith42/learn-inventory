package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public interface Item {
	
	boolean canOnlyBeOrderedOnFirstOfTheMonth();
	
	int getShouldHave();
	
	void setRequiredOnHand(int newAmount);

	Optional<Order> createOrder(LocalDate when, InventoryDatabase database, MarketingInfo marketingInfo);
	
}
