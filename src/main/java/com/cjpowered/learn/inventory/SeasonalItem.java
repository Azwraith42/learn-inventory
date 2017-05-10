package com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class SeasonalItem implements Item {

	private final int wantOnHand;
	private final Season season;
	
	public SeasonalItem(final int wantOnHand, final Season season){
		this.wantOnHand = wantOnHand;
		this.season = season;
	}

	@Override
	public Order createOrder(final LocalDate when, InventoryDatabase database, MarketingInfo marketingInfo) {
		final int onHand = database.onHand(this);
		final boolean inSeason = season.equals(marketingInfo.season(when));
		final boolean onSale = marketingInfo.onSale(this);
		final int toOrder;
		if(inSeason){
			if(onSale && wantOnHand < 20){
				toOrder = (20+wantOnHand) - onHand;
			}else{
				toOrder = (wantOnHand*2) - onHand;
			}
		}else{
			 toOrder = wantOnHand - onHand;
		}
		return new Order(this, toOrder);
	}

}
