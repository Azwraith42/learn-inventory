package com.cjpowered.learn.inventory;

import com.cjpowered.learn.marketing.Season;

public class StockedItem implements Item {
	
	private int wantOnHand;
	private Season season;
	
	public StockedItem(int wantOnHand, Season season){
		this.wantOnHand = wantOnHand;
		this.season = season;
	}
	
	@Override
	public int wantOnHand(){
		return wantOnHand;
	}
	
	@Override
	public Season season(){
		return season;
	}
	
	
}
