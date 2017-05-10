package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private final int wantOnHand;
	private final boolean canOnlyBeOrderedOnFirstOfTheMonth;
	private final int ammountInABunch;
	
	public StockedItem(final int wantOnHand){
		this.wantOnHand = wantOnHand;
		this.canOnlyBeOrderedOnFirstOfTheMonth = false;
		this.ammountInABunch = 1;
	}
	
	public StockedItem(final int wantOnHand, final boolean canOnlyBeOrderedOnFirstOfTheMonth){
		this.wantOnHand = wantOnHand;
		this.canOnlyBeOrderedOnFirstOfTheMonth = canOnlyBeOrderedOnFirstOfTheMonth;
		this.ammountInABunch = 1;
	}
	
	public StockedItem(final int wantOnHand, final int ammountInABunch){
		this.wantOnHand = wantOnHand;
		this.canOnlyBeOrderedOnFirstOfTheMonth = false;
		this.ammountInABunch = ammountInABunch;
	}
	
	@Override
	public boolean canOnlyBeOrderedOnFirstOfTheMonth(){
		return this.canOnlyBeOrderedOnFirstOfTheMonth;
	}
	
	@Override
	public Optional<Order> createOrder(final LocalDate when, final InventoryDatabase database, final MarketingInfo marketingInfo){
		final int onHand = database.onHand(this);
		final boolean onSale = marketingInfo.onSale(this);
		final int toOrder;
		if(onSale){
			toOrder = wantOnHand + 20 - onHand;
		}else{
			 toOrder = wantOnHand - onHand;
		}
		
		if(ammountInABunch == 1 || toOrder % ammountInABunch == 0){
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, toOrder));
		}else{
			final int numberOfBunches = (int)Math.ceil((float)toOrder / (float)ammountInABunch);
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, numberOfBunches*ammountInABunch));
		}
		
	}
}
