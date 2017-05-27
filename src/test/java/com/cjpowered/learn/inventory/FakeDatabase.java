package com.cjpowered.learn.inventory;

import com.google.common.collect.Table;

import java.util.*;

public class FakeDatabase implements InventoryDatabase {

    private final Table<Warehouse, Item, Integer> dataStore;
    private final Table<Warehouse, Item, Integer> itemsOnOrder;
    private final Map<String, Integer> callCounter;

    public static final String ON_HAND_METHOD = "ON_HAND_METHOD";


    public FakeDatabase(final Table<Warehouse, Item, Integer> dataStore, final Table<Warehouse, Item, Integer> itemsOnOrder) {
        this(dataStore, itemsOnOrder, new HashMap<>());
    }

    public FakeDatabase(final Table<Warehouse, Item, Integer> dataStore, final Table<Warehouse, Item, Integer> itemsOnOrder, Map<String, Integer> callCounter) {
        this.dataStore = dataStore;
        this.itemsOnOrder = itemsOnOrder;
        this.callCounter = callCounter;
        callCounter.put(ON_HAND_METHOD, 0);
    }


    @Override
    public int onHand(Item item) {
        return onHand(item, Warehouse.home());
    }

    @Override
    public int onHand(Item item, Warehouse warehouse) {
        return Optional.ofNullable(dataStore.get(warehouse, item)).orElse(0);
    }

    @Override
    public List<Item> stockItems() {
        return new ArrayList<>(dataStore.columnKeySet());
    }

    @Override
    public int onOrder(Item item) {
        return onOrder(item, Warehouse.home());
    }

    @Override
    public int onOrder(Item item, Warehouse warehouse) {
        return Optional.ofNullable(itemsOnOrder.get(warehouse, item)).orElse(0);

    }

    @Override
    public void setRequiredOnHand(Item item, int newAmount) {
        setRequiredOnHand(item, Warehouse.home(), newAmount);
    }

    @Override
    public void setRequiredOnHand(Item item, Warehouse warehouse, int newAmount) {
        final Integer onHand = callCounter.get(ON_HAND_METHOD);
        callCounter.replace(ON_HAND_METHOD, onHand, onHand + 1);
    }

}
