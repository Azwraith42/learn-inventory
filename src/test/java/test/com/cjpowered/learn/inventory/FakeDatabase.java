package test.com.cjpowered.learn.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;

public class FakeDatabase implements InventoryDatabase {

	private final Map<Item, Integer> dataStore;
	private final Map<Item, Integer> ordering;
	
	public FakeDatabase(final Map<Item, Integer> dataStore, final Map<Item, Integer> ordering){
		this.dataStore = dataStore;
		this.ordering = ordering;
	}
	
	@Override
	public int onHand(Item item) {
		return dataStore.get(item);
	}

	@Override
	public List<Item> stockItems() {
		final Set<Item> keys = dataStore.keySet();
		return new ArrayList<>(keys);
	}
	
	@Override
	public int onOrder(Item item){
		if(ordering.containsKey(item)){
			return ordering.get(item);
		}else{
			return 0;
		}
	}
	
	@Override
	public void setRequiredOnHand(Item item, int newAmount) {
		item.setRequiredOnHand(newAmount);
		
	}

}
