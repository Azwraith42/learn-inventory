package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private int wantOnHand;
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
	public int getShouldHave() {
		return wantOnHand;
	}
	
	@Override
	public void setRequiredOnHand(int newAmount) {
		wantOnHand = newAmount;
		
	}
	
	@Override
	public Optional<Order> createOrder(final LocalDate when, final InventoryDatabase database, final MarketingInfo marketingInfo){
		final int onOrder = database.onOrder(this);
		final int onHand = database.onHand(this);
		final int total = onHand + onOrder;
		final boolean onSale = marketingInfo.onSale(this);
		final int toOrder;
		
		if(total == 0){
			final int newAmount;
			
			newAmount = (int) (wantOnHand*1.1);
			
			database.setRequiredOnHand(this, newAmount);
		}
		
		if(onSale){
			if( (float)(total)/(float)(wantOnHand+20) > 0.80){return Optional.empty();}
			toOrder = wantOnHand + 20 - total;
		}else{
			if( (float)(total)/(float)(wantOnHand) > 0.80){return Optional.empty();}
			 toOrder = wantOnHand - total;
		}
		
		if(ammountInABunch == 1 || toOrder % ammountInABunch == 0){
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, toOrder));
		}else{
			final int numberOfBunches = (int)Math.ceil((float)toOrder / (float)ammountInABunch);
			if( (numberOfBunches*ammountInABunch) + total > wantOnHand ){
				return (numberOfBunches == 1) ? Optional.empty() : Optional.of(new Order(this, (numberOfBunches-1)*ammountInABunch ));
			}
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, numberOfBunches*ammountInABunch));
		}
		
	}
}
