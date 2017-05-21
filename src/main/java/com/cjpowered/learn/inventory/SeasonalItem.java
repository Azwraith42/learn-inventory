package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class SeasonalItem implements Item {

	private final int wantOnHand;
	private final Season season;
	private final boolean canOnlyBeOrderedOnFirstOfTheMonth;
	private final int ammountInABunch;
	
	public SeasonalItem(final int wantOnHand, final Season season){
		this.wantOnHand = wantOnHand;
		this.season = season;
		this.canOnlyBeOrderedOnFirstOfTheMonth = false;
		this.ammountInABunch = 1;
	}
	
	public SeasonalItem(final int wantOnHand, final Season season, final boolean canOnlyBeOrderedOnFirstOfTheMonth){
		this.wantOnHand = wantOnHand;
		this.season = season;
		this.canOnlyBeOrderedOnFirstOfTheMonth = canOnlyBeOrderedOnFirstOfTheMonth;
		this.ammountInABunch = 1;
	}
	
	public SeasonalItem(final int wantOnHand, final Season season, final int ammountInABunch){
		this.wantOnHand = wantOnHand;
		this.season = season;
		this.canOnlyBeOrderedOnFirstOfTheMonth = false;
		this.ammountInABunch = ammountInABunch;
	}
	
	@Override
	public boolean canOnlyBeOrderedOnFirstOfTheMonth(){
		return this.canOnlyBeOrderedOnFirstOfTheMonth;
	}

	@Override
	public Optional<Order> createOrder(final LocalDate when, InventoryDatabase database, MarketingInfo marketingInfo) {
		final int onHand = database.onHand(this);
		final boolean inSeason = season.equals(marketingInfo.season(when));
		final boolean onSale = marketingInfo.onSale(this);
		final int onOrder = database.onOrder(this);
		final int total = onHand + onOrder;
		final int toOrder;
		
		
		if(inSeason){
			if(onSale && wantOnHand < 20){
				if( (float)(onHand+onOrder)/(float)(wantOnHand+20) > 0.80){return Optional.empty();}
				toOrder = (20+wantOnHand) - total;
			}else{
				if( (float)(onHand+onOrder)/(float)(wantOnHand*2) > 0.80){return Optional.empty();}
				toOrder = (wantOnHand*2) - total;
			}
		}else{
			if( (float)(onHand+onOrder)/(float)(wantOnHand) > 0.80){return Optional.empty();}
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
