package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class SeasonalItem implements Item {

	private final Map<Warehouse, Integer> wantOnHand;
	private final Season season;
	private final int ammountInABunch;
	private final Schedule schedule;
	
	public SeasonalItem(final Map<Warehouse, Integer> wantOnHand, final Season season, final Schedule schedule){
		this.wantOnHand = wantOnHand;
		this.schedule = schedule;
		this.ammountInABunch = 1;
		this.season = season;
	}
	
	@Deprecated
	public SeasonalItem(final int wantOnHand, final Season season, final Schedule schedule){
		
		this(wantOnHand, season, schedule, 1);
	}
	
	@Deprecated
	public SeasonalItem(final int wantOnHand, final Season season, final Schedule schedule, final int ammountInABunch){
		this.wantOnHand = new HashMap<>();
		this.wantOnHand.put(Warehouse.home(), wantOnHand);
		this.season = season;
		this.schedule = schedule;
		this.ammountInABunch = ammountInABunch;
	}
	
	@Override
	public boolean canOrder(LocalDate today){
		return schedule.canOrderToday(today);
	}

	@Override
	public Optional<Order> createOrder(final LocalDate when, InventoryDatabase database, MarketingInfo marketingInfo) {
		return createOrder(when, database, marketingInfo, Warehouse.home());
	}

	@Override
	public Optional<Order> createOrder(LocalDate when, InventoryDatabase database, MarketingInfo marketingInfo, Warehouse warehouse) {
		final int onHand = database.onHand(this, warehouse);
		final boolean inSeason = season.equals(marketingInfo.season(when));
		final boolean onSale = marketingInfo.onSale(this, when);
		final int onOrder = database.onOrder(this, warehouse);
		final int total = onHand + onOrder;
		final int toOrder;

		if(!wantOnHand.containsKey(warehouse) || wantOnHand.get(warehouse) == total || wantOnHand.get(warehouse)  == 0){
			return Optional.empty();
		}
		
		if(total == 0){
			final int newAmount;
			newAmount = (int) Math.ceil(wantOnHand.get(warehouse)*1.1);
			database.setRequiredOnHand(this, warehouse, newAmount);
		}

		if(inSeason){
			if(onSale && wantOnHand.get(warehouse) < 20){
				if( (float)(total)/(float)(wantOnHand.get(warehouse)+20) > 0.80){return Optional.empty();}
				toOrder = (20+wantOnHand.get(warehouse)) - total;
			}else{
				if( (float)(total)/(float)(wantOnHand.get(warehouse)*2) > 0.80){return Optional.empty();}
				toOrder = (wantOnHand.get(warehouse)*2) - total;
			}
		}else{
			if( (float)(onHand+onOrder)/(float)(wantOnHand.get(warehouse)) > 0.80){return Optional.empty();}
			toOrder = wantOnHand.get(warehouse) - total;

		}

		if(ammountInABunch == 1 || toOrder % ammountInABunch == 0){
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, toOrder, warehouse));
		}else{
			final int numberOfBunches = (int)Math.ceil((float)toOrder / (float)ammountInABunch);
			if( (numberOfBunches*ammountInABunch) + total > wantOnHand.get(warehouse) ){
				return (numberOfBunches == 1) ? Optional.empty() : Optional.of(new Order(this, (numberOfBunches-1)*ammountInABunch, warehouse ));
			}
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, numberOfBunches*ammountInABunch, warehouse));
		}
	}

}
