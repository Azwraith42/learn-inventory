package com.cjpowered.learn.inventory;

/**
 * An item we can stock. Any instance data will be provided by by the database
 * when you invoke {@link InventoryDatabase#stockItems()}
 *
 */

import com.cjpowered.learn.marketing.MarketingInfo;

import java.time.LocalDate;
import java.util.Optional;

public interface Item {
	
	boolean canOrder(LocalDate today);
	
	int getShouldHave();
	
	void setRequiredOnHand(int newAmount);

	Optional<Order> createOrder(LocalDate when, InventoryDatabase database, MarketingInfo marketingInfo);
	
}
