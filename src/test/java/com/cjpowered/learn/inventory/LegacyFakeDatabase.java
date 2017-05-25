package com.cjpowered.learn.inventory;

import java.util.*;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Warehouse;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

@Deprecated
public class LegacyFakeDatabase extends FakeDatabase {

	private static Table<Warehouse, Item, Integer> mapToTable(Map<Item, Integer> thing) {
		Table<Warehouse, Item, Integer> newThing = HashBasedTable.create();
		for (Map.Entry<Item, Integer> entry : thing.entrySet()) {
			newThing.put(Warehouse.home(), entry.getKey(), entry.getValue());
		}
		return newThing;
	}

	public LegacyFakeDatabase(final Map<Item, Integer> dataStore, final Map<Item, Integer> ordering){
		this(dataStore, ordering, new HashMap<>());
	}
	public LegacyFakeDatabase(final Map<Item, Integer> dataStore, final Map<Item, Integer> ordering, Map<String, Integer> callCounter){
		super(mapToTable(dataStore), mapToTable(ordering), callCounter);
	}
	
}
