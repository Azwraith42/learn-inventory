package com.cjpowered.learn.inventory;

/**
 * An item we can stock. Any instance data will be provided by by the database
 * when you invoke {@link InventoryDatabase#stockItems()}
 *
 */
import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

import java.time.LocalDate;

public interface Item {

	Optional<Order> createOrder(LocalDate when, InventoryDatabase database, MarketingInfo marketingInfo);
	
}
