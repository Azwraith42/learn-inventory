package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private final Map<Warehouse, Integer> wantOnHand;
	private final int ammountInABunch;
	private final Schedule schedule;

	@Deprecated
	public StockedItem(final int wantOnHand, final Schedule schedule){
		this(wantOnHand, schedule, 1);
	}

	public StockedItem(final Map<Warehouse, Integer> wantOnHand, final Schedule schedule){
		this.wantOnHand = wantOnHand;
		this.schedule = schedule;
		this.ammountInABunch = 1;
	}

	@Deprecated
	public StockedItem(final int wantOnHand,final Schedule schedule, final int ammountInABunch){
		this.wantOnHand = new HashMap<>();
		this.wantOnHand.put(Warehouse.home(), wantOnHand);
		this.schedule = schedule;
		this.ammountInABunch = ammountInABunch;
	}
	
	@Override
	public boolean canOrder(LocalDate today){
		return schedule.canOrderToday(today);
	}

	@Override
	public Optional<Order> createOrder(final LocalDate when, final InventoryDatabase database, final MarketingInfo marketingInfo){
		return createOrder(when, database, marketingInfo, Warehouse.home());
	}

	@Override
	public Optional<Order> createOrder(final LocalDate when, final InventoryDatabase database, final MarketingInfo marketingInfo, Warehouse warehouse){
		final int onOrder = database.onOrder(this, warehouse);
		final int onHand = database.onHand(this, warehouse);
		final int total = onHand + onOrder;
		final boolean onSale = marketingInfo.onSale(this, when);
		final int toOrder;


		if(total == 0){
			final int newAmount;

			newAmount = (int)Math.ceil(wantOnHand.get(warehouse)*1.1);

			database.setRequiredOnHand(this, warehouse, newAmount);
		}

		if(onSale){
			if( (float)(total)/(float)(wantOnHand.get(warehouse)+20) > 0.80){return Optional.empty();}
			toOrder = wantOnHand.get(warehouse) + 20 - total;
		}else{
			if( (float)(total)/(float)(wantOnHand.get(warehouse)) > 0.80){return Optional.empty();}
			 toOrder = wantOnHand.get(warehouse) - total;
		}

		if(ammountInABunch == 1 || toOrder % ammountInABunch == 0){
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, toOrder, warehouse));
		}else{
			final int numberOfBunches = (int)Math.ceil((float)toOrder / (float)ammountInABunch);
			if( (numberOfBunches*ammountInABunch) + total > wantOnHand.get(warehouse) ){
				return (numberOfBunches == 1) ? Optional.empty() : Optional.of(new Order(this, (numberOfBunches-1)*ammountInABunch , warehouse));
			}
			return (toOrder < 1) ? Optional.empty() : Optional.of(new Order(this, numberOfBunches*ammountInABunch, warehouse));
		}

	}
}
