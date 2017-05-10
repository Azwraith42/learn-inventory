package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.inventory.SeasonalItem;
import com.cjpowered.learn.inventory.StockedItem;
import com.cjpowered.learn.inventory.ace.AceInventoryManager;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

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
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
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
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final InventoryDatabase db = new FakeDatabase(store);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return false;
    		}@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	
    	//then
    	Order expectedOrder = new Order(item, shouldHave - onHand);
    	Set<Order> expected = Collections.singleton(expectedOrder);
    	assertEquals(expected, new HashSet<>(actual));
    	
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
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
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
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
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
    public void ifWeHaveMoreThanShouldHaveButStillNeedMoreBecauseOnSale(){
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
    			return true;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	// when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals( (shouldHave+20) - onHand, actual.get(0).quantity);
    	
    	
    }
    
    @Test
    public void ifOnSaleKeepExtraTwenty(){
    	//given
    	final int onHand = 9;
    	final int shouldHave = 15;
    	final boolean onSale = true;
    	Item item = new StockedItem(shouldHave);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final InventoryDatabase db = new FakeDatabase(store);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return onSale;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	final Order expectedOrder = new Order(item, 20 + shouldHave - onHand);
    	final Set<Order> expected = Collections.singleton(expectedOrder);
    	assertEquals(expected, new HashSet<>(actual));
    	
    }
    
    @Test
    public void keepDoubleInventoryIfInSeason(){
    	//given
    	final int onHand = 7;
    	final int shouldHave = 22;
    	final Season season = Season.Fall;
    	final boolean onSale = false;
    	Item item = new SeasonalItem(shouldHave, season);
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
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return onSale;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return season;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals( (shouldHave*2) - onHand, actual.get(0).quantity);
    }

    @Test
    public void onSaleAndInSeasonAdditionLarger(){
    	//given
    	final int onHand = 3;
    	final int shouldHave = 15;
    	final Season season = Season.Fall;
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
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return onSale;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals( (shouldHave+20) - onHand, actual.get(0).quantity);
    }
    
    @Test
    public void onSaleAndInSeasonMultiplicationLarger(){
    	//given
    	final int onHand = 8;
    	final int shouldHave = 25;
    	final Season season = Season.Fall;
    	final boolean onSale = true;
    	Item item = new SeasonalItem (shouldHave, season);
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
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item){
    			return onSale;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals( (shouldHave*2) - onHand, actual.get(0).quantity);
    }
    
    
    
    // ifOnSaleAndEqualToBaseInventoryQuantity
    
    // ifOnSaleAndLessThanBaseInventoryQuantity
    
    // ifOnSaleAndMoreThanBaseInventoryQuantity
    
    // ifOnSaleAndEqualToSaleInventoryQuantity
    
    // ifOnSaleAndLessThanSaleInventoryQuantityButMoreThanBase
    
    // ifOnSaleAndMoreThanSaleInventoryQuantity
    
    
    //// ifInSeasonAndEqualToBaseInventoryQuantity
    
    // ifInSeasonAndLessThanBaseInventoryQuantity
    
    // ifInSeasonAndMoreThanBaseInventoryQuantity
    
    // ifInSeasonAndEqualToSaleInventoryQuantity
    
    // ifInSeasonAndLessThanSaleInventoryQuantityButMoreThanBase
    
    // ifInSeasonAndMoreThanSaleInventoryQuantity
    
    //// ifOnSaleAndInSeasonEqualToBaseInventoryQuantitySaleBetter
    
	 // ifOnSaleAndInSeasonLessThanBeaseInventoryQuantitySaleBetter
	    
	 // ifOnSaleAndInSeasonMoreThanBaseInventoryQuantitySaleBetter
	    
	 // ifOnSaleAndInSeasonEqualToBaseInventoryQuantitySeasonBetter
	    
	 // ifOnSaleAndInSeasonLessThanBeaseInventoryQuantitySeasonBetter
	    
	 // ifOnSaleAndInSeasonMoreThanBaseInventoryQuantitySeasonBetter
    
    
    //// ifOnSaleAndInSeason
	    
	 // ifOnSaleAndInSeason
    
	 // ifOnSaleAndInSeason
	 
    // ifOnSaleAndInSeason
	 
    // ifOnSaleAndInSeason
	 
    // ifOnSaleAndInSeason
}
