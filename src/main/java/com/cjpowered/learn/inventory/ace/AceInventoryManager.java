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
	
	public AceInventoryManager(final InventoryDatabase database, final MarketingInfo marketingInfo){
		this.database = database;
		this.marketingInfo = marketingInfo;
	}
	
    @Override
    public List<Order> getOrders(final LocalDate today) {
    	 
    	 final List<Order> orders = new ArrayList<>();
    	 final List<Item> items = database.stockItems();
    	 for(Item item : items){
    		 final Order order = item.createOrder(today, database, marketingInfo);
    		 if(order.quantity > 0){
    			 orders.add(order);
    		 }
    	 }
//    	 for(Item item : items){
//    		 int onHand = database.onHand(item);
//    		 int wantOnHand = item.wantOnHand();
//    		 final int toOrder;
//    		 
//    		 boolean onSale = marketingInfo.onSale(item);
//    		 boolean inSeason = marketingInfo.season(LocalDate.now()).equals(item.season());
//    		 
//    		 if(onSale && inSeason){
//    			 if(wantOnHand > 20){
//    				 wantOnHand = wantOnHand*2;
//    			 }else{
//    				 wantOnHand += 20;
//    			 }
//    		 }else{
//    			 if(onSale ){
//        			 wantOnHand += 20;
//        		 }
//        		 if(inSeason ){
//        			 wantOnHand = wantOnHand*2;
//        		 }
//    		 }
//    		 
//    		 toOrder = wantOnHand - onHand;
//    		 if(toOrder > 0){
//    			 final Order order = new Order(item, toOrder);
//        		 orders.add(order);
//    		 }
//    		 
//    		 
//    		 
//    	 }
    	 return orders;
    }

}
