package com.cjpowered.learn.inventory;

public class StockedItem implements Item {
	
	private int wantOnHand;
	
	public StockedItem(int wantOnHand){
		this.wantOnHand = wantOnHand;
	}
	
	@Override
	public int wantOnHand(){
		return wantOnHand;
	}
	
	
}
