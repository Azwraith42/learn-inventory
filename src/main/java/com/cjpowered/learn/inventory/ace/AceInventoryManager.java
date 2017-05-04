package com.cjpowered.learn.inventory.ace;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.marketing.MarketingInfo;

public final class AceInventoryManager implements InventoryManager {

	private final InventoryDatabase database;
	private final MarketingInfo marketingInfo;
	
	public AceInventoryManager(InventoryDatabase database, MarketingInfo marketingInfo){
		this.database = database;
		this.marketingInfo = marketingInfo;
	}
	
     @Override
    public List<Order> getOrders(final LocalDate today) {
    	 
    	 final List<Order> orders = new ArrayList<>();
    	 final List<Item> items = database.stockItems();
    	 for(Item item : items){
    		 int onHand = database.onHand(item);
    		 int wantOnHand = item.wantOnHand();
    		 
    		 boolean onSale = marketingInfo.onSale(item);
    		 boolean inSeason = marketingInfo.season(LocalDate.now()) == item.season();
    		 
    		 if(onSale && inSeason){
    			 if(wantOnHand > 20){
    				 wantOnHand = wantOnHand*2;
    			 }else{
    				 wantOnHand += 20;
    			 }
    		 }else{
    			 if(onSale ){
        			 wantOnHand += 20;
        		 }
        		 if(inSeason ){
        			 wantOnHand = wantOnHand*2;
        		 }
    		 }
    		 
    		 int toOrder = wantOnHand - onHand;
    		 if(toOrder > 0){
    			 final Order order = new Order(item, toOrder);
        		 orders.add(order);
    		 }
    		 
    		 
    		 
    	 }
    	 return orders;
    }

}
