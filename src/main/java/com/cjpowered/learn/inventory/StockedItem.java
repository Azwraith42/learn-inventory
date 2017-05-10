package com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private final int wantOnHand;
	
	public StockedItem(final int wantOnHand){
		this.wantOnHand = wantOnHand;
	}
	
	@Override
	public Order createOrder(final LocalDate when, final InventoryDatabase database, final MarketingInfo marketingInfo){
		final int onHand = database.onHand(this);
		final boolean onSale = marketingInfo.onSale(this);
		final int toOrder;
		if(onSale){
			toOrder = wantOnHand + 20 - onHand;
		}else{
			 toOrder = wantOnHand - onHand;
		}
		return new Order(this, toOrder);
	}
}
