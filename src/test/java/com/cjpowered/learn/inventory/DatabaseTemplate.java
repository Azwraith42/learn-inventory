package test.com.cjpowered.learn.inventory;

import java.util.List;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Warehouse;

public class DatabaseTemplate implements InventoryDatabase {

    @Override
    public int onHand(final Item item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Item> stockItems() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onOrder(Item item){
    	throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onOrder(Item item, Warehouse warehouse) {
        return 0;
    }

    @Override
    public void setRequiredOnHand(Item item, int newAmount) {
    	throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setRequiredOnHand(Item item, Warehouse warehouse, int newAmount) {

    }

    @Override
    public int onHand(Item item, Warehouse warehouse) {
        return 0;
    }
}
