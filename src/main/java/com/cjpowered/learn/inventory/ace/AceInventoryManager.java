package com.cjpowered.learn.inventory.ace;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.cjpowered.learn.inventory.*;
import com.cjpowered.learn.marketing.MarketingInfo;

public final class AceInventoryManager implements InventoryManager {

	private final InventoryDatabase database;
	private final MarketingInfo marketingInfo;
	
	public AceInventoryManager(final InventoryDatabase database, final MarketingInfo marketingInfo){
		this.database = database;
		this.marketingInfo = marketingInfo;
	}
	
    public List<Order> getOrders(final LocalDate today) {
    	 
    	 final List<Order> orders = new ArrayList<>();
    	 final List<Item> items = database.stockItems();

    	 for(Warehouse warehouse : Warehouse.values()){
	    	 for(Item item : items){
	    		 if(item.canOrder(today) ){
		    		 final Optional<Order> order = item.createOrder(today, database, marketingInfo, warehouse);
		    		 if(order.isPresent()){
		    			 orders.add(order.get());
		    		 }
	    		 }
	    	 }
    	 }
    	 return orders;
    }

}
