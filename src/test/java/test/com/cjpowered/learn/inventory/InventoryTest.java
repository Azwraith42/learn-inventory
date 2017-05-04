package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.inventory.StockedItem;
import com.cjpowered.learn.inventory.ace.AceInventoryManager;

/*
 * We need to keep items in stock to prevent back orders. See the README.md
 * for the requirements.
 *
 */

public class InventoryTest {

    @Test
    public void whenNoStockItemsDoNotOrder() {
        // given
    	final InventoryDatabase db = new DatabaseTemplate(){
    	
    		@Override
    		public List<Item> stockItems(){
    			return Collections.emptyList();
    		}
    	};
    	final MarketingTemplate mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return true;
    		}
    	};
        final LocalDate today = LocalDate.now();
        final InventoryManager im = new AceInventoryManager(db, mi);

        // when
        final List<Order> actual = im.getOrders(today);

        // then
        assertTrue(actual.isEmpty());

    }
    
    @Test
    public void orderEnoughStock(){
    	// given
    	final int onHand = 10;
    	final int shouldHave = 16;
    	Item item = new StockedItem(shouldHave);
    	final InventoryDatabase db = new DatabaseTemplate() {
    		@Override
    		public int onHand(Item item) {
    			return onHand;
    		}
    		@Override
    		public List<Item> stockItems() {
    			return Collections.singletonList(item);
    		}
    	};
    	final MarketingTemplate mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return false;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals(shouldHave-onHand, actual.get(0).quantity );
    	
    }
    
    @Test
    public void doNotOrderIfHaveMore(){
    	// given
    	final int onHand = 10;
    	final int shouldHave = 9;
    	Item item = new StockedItem(shouldHave);
    	final InventoryDatabase db = new DatabaseTemplate(){
    		@Override
    		public int onHand(Item item) {
    			return onHand;
    		}
    		@Override
    		public List<Item> stockItems() {
    			return Collections.singletonList(item);
    		}
    	};
    	final MarketingTemplate mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return false;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	// when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertTrue(actual.isEmpty());
    	
    }
    
    @Test
    public void doNotAddIfEqual(){
    	// given
    	final int onHand = 10;
    	final int shouldHave = onHand;
    	Item item = new StockedItem(shouldHave);
    	final InventoryDatabase db = new DatabaseTemplate(){
    		@Override
    		public int onHand(Item item) {
    			return onHand;
    		}
    		@Override
    		public List<Item> stockItems() {
    			return Collections.singletonList(item);
    		}
    	};
    	final MarketingTemplate mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return false;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	// when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertTrue(actual.isEmpty());
    	
    }
    
    @Test
    public void ifOnSaleKeepExtraTwenty(){
    	//given
    	final int onHand = 9;
    	final int shouldHave = 15;
    	final boolean onSale = true;
    	Item item = new StockedItem(shouldHave);
    	final InventoryDatabase db = new DatabaseTemplate(){
    		@Override
    		public int onHand(Item item) {
    			return onHand;
    		}
    		@Override
    		public List<Item> stockItems() {
    			return Collections.singletonList(item);
    		}
    	};
    	final MarketingTemplate mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return true;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals(shouldHave+20-onHand, actual.get(0).quantity );
    }

}
