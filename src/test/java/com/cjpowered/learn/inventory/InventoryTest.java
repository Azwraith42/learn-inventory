package com.cjpowered.learn.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.junit.Test;

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
    		public boolean onSale(final Item item, final LocalDate when){
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
		final Map<String, Integer> callCounter = new HashMap<>();
    	final int onHand = 10;
    	final int shouldHave = 16;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
    	final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
    			return false;
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
    	Order expectedOrder = new Order(item, shouldHave - onHand);
    	Set<Order> expected = Collections.singleton(expectedOrder);
    	assertEquals(expected, new HashSet<>(actual));
    	
    }
    
    @Test
    public void doNotOrderIfHaveMore(){
    	// given
    	final int onHand = 10;
    	final int onOrder = 0;
    	final int shouldHave = 9;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
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
    	final int onOrder = 0;
    	final int shouldHave = onHand;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
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
    	final int onOrder = 0;
    	final int shouldHave = 9;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
    	final MarketingTemplate mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
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
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
    	final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
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
    public void onSaleAndInSeasonAdditionLarger(){
    	//given
    	final int onHand = 3;
    	final int onOrder = 0;
    	final int shouldHave = 15;
    	final Season season = Season.Fall;
    	final boolean onSale = true;
    	final Schedule schedule = new AnyDay();
    	Item item = new SeasonalItem(shouldHave, season, schedule);
    	final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
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
    	assertEquals( (shouldHave+20) - onHand, actual.get(0).quantity);
    }
    
    @Test
    public void onSaleAndInSeasonMultiplicationLarger(){
    	//given
    	final int onHand = 8;
    	final int onOrder = 0;
    	final int shouldHave = 25;
    	final Season season = Season.Fall;
    	final boolean onSale = true;
    	final Schedule schedule = new AnyDay();
    	Item item = new SeasonalItem (shouldHave, season, schedule);
    	final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
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
	public void keepDoubleInventoryIfInSeason(){
		//given
		final int onHand = 7;
		final int onOrder = 0;
		final int shouldHave = 22;
		final Season season = Season.Fall;
		final boolean onSale = false;
		final Schedule schedule = new AnyDay();
		Item item = new SeasonalItem(shouldHave, season, schedule);
		final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(Warehouse.home(), item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
		final MarketingInfo mi = new MarketingTemplate(){
			@Override
			public boolean onSale(final Item item, final LocalDate when){
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
    public void doNotOrderIfNotFirstOfTheMonth(){
    	//given
    	final int onHand = 8;
    	final int shouldHave = 15;
    	final int onOrder = 0;
    	final Schedule schedule = new FirstOfTheMonth();
    	Item item = new StockedItem(shouldHave, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
		final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate notFirst = LocalDate.of(2017, Month.JUNE, 2);
    	
    	//when
    	final List<Order> actual = im.getOrders(notFirst);
    	
    	//then
    	assertTrue(actual.isEmpty());
    }
    
    @Test
    public void doNotOrderBunchesIfItWillBringUsAboveWantOnHand(){
    	//given
    	final int onHand = 8;
    	final int shouldHave = 15;
    	final int ammountInABunch = 6;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule, ammountInABunch);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
    	final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate notFirst = LocalDate.of(2017, Month.JUNE, 2);
    	
    	//when
    	final List<Order> actual = im.getOrders(notFirst);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals( 6, actual.get(0).quantity);
    }
    
    @Test
    public void doNotOrderBunchesOfSeasonalItemsIfItWillBringUsAboveWantOnHand(){
    	//given
    	final int onHand = 8;
    	final int shouldHave = 15;
    	final int ammountInABunch = 6;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	final Season season = Season.Fall;
    	Item item = new SeasonalItem(shouldHave, season, schedule, ammountInABunch);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
    	final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(final Item item, final LocalDate when){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when){
    			return season;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate notFirst = LocalDate.of(2017, Month.JUNE, 2);
    	
    	//when
    	final List<Order> actual = im.getOrders(notFirst);
    	
    	//then
    	assertEquals(1, actual.size());
    	assertEquals(item, actual.get(0).item);
    	assertEquals( 18, actual.get(0).quantity);
    }
    
    @Test
    public void doNotOrderIfAlreadyOnOrder(){
    	//given
    	final int onHand = 5;
    	final int shouldHave = 10;
    	final int onOrder = 5;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item,  onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
    	final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when) {
    			return false;
    		};
    		@Override
    		public Season season(LocalDate when) {
    			return Season.Fall;
    		};
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
        assertTrue(actual.isEmpty());
    }
    
    @Test
    public void doNotOrderSeasonalItemIfAlreadyOnOrder(){
    	//given
    	final int onHand = 5;
    	final int shouldHave = 10;
    	final int onOrder = 15;
    	final Schedule schedule = new AnyDay();
    	final Season season = Season.Fall;
    	Item item = new SeasonalItem(shouldHave, season, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item,  onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
		final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when) {
    			return false;
    		};
    		@Override
    		public Season season(LocalDate when) {
    			return season;
    		};
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
        assertTrue(actual.isEmpty());
    }
    
    @Test
    public void doNotOrderIfWeHaveOverEightyPercent(){
    	//given
    	final int onHand = 9;
    	final int shouldHave = 10;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
    	final Map<String, Integer> callCounter = new HashMap<>();
		final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when) {
    			return false;
    		};
    		@Override
    		public Season season(LocalDate when) {
    			return Season.Fall;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertTrue(actual.isEmpty());
    }
    
    @Test
    public void doNotOrderSeasonalIfWeHaveOverEightyPercent(){
    	//given
    	final int onHand = 36;
    	final int shouldHave = 20;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	final Season season = Season.Winter;
    	Item item = new SeasonalItem(shouldHave, season, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item, onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
    	final InventoryDatabase db = new LegacyFakeDatabase(store, ordering);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when) {
    			return false;
    		};
    		@Override
    		public Season season(LocalDate when) {
    			return season;
    		}
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertTrue(actual.isEmpty());
    }
    
    @Test
    public void ifWeRunOutIncreaseOnHandByTenPercent(){
    	//given
    	final int onHand = 0;
    	final int shouldHave = 10;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item,  onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
		final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when) {
    			return Season.Fall;
    		};
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
		assertEquals((Integer)1, callCounter.get(FakeDatabase.ON_HAND_METHOD));
    }
    
    @Test
    public void ifWeRunOutOfSeasonalIncreaseOnHandByTenPercent(){
    	//given
    	final int onHand = 0;
    	final int shouldHave = 10;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	final Season season = Season.Winter;
    	Item item = new SeasonalItem(shouldHave, season, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item,  onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
    	final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when) {
    			return season;
    		};
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
    	assertEquals((Integer)1, callCounter.get(FakeDatabase.ON_HAND_METHOD));
    }
    
    @Test
    public void ifWeRunOutIncreaseOnHandByTenPercentRoundedUp(){
    	//given
    	final int onHand = 0;
    	final int shouldHave = 19;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	Item item = new StockedItem(shouldHave, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item,  onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
		final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when) {
    			return Season.Fall;
    		};
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
		assertEquals((Integer)1, callCounter.get(FakeDatabase.ON_HAND_METHOD));
	}
    
    @Test
    public void ifWeRunOutOfSeasonalIncreaseOnHandByTenPercentRoundedUp(){
    	//given
    	final int onHand = 0;
    	final int shouldHave = 19;
    	final int onOrder = 0;
    	final Schedule schedule = new AnyDay();
    	final Season season = Season.Winter;
    	Item item = new SeasonalItem(shouldHave, season, schedule);
    	final HashMap<Item, Integer> store = new HashMap<>();
    	store.put(item,  onHand);
    	final HashMap<Item, Integer> ordering = new HashMap<>();
    	ordering.put(item, onOrder);
		final Map<String, Integer> callCounter = new HashMap<>();
		final InventoryDatabase db = new LegacyFakeDatabase(store, ordering, callCounter);
    	final MarketingInfo mi = new MarketingTemplate(){
    		@Override
    		public boolean onSale(Item item, final LocalDate when){
    			return false;
    		}
    		@Override
    		public Season season(LocalDate when) {
    			return season;
    		};
    	};
    	final InventoryManager im = new AceInventoryManager(db, mi);
    	final LocalDate today = LocalDate.now();
    	
    	//when
    	final List<Order> actual = im.getOrders(today);
    	
    	//then
		assertEquals((Integer)1, callCounter.get(FakeDatabase.ON_HAND_METHOD));

	}

    @Test
	public void orderFromOtherWarehouses(){
    	//given
		final Integer onHand = 5;
		final Integer shouldHave = 15;
		final Integer onOrder = 0;
		final Warehouse warehouse = Warehouse.Ashford;
		final Schedule schedule = new AnyDay();
		final Map<Warehouse, Integer> ammountNeededInLocation = new HashMap<>();
		ammountNeededInLocation.put(warehouse, shouldHave);
		final Item item = new StockedItem(ammountNeededInLocation, schedule);
		final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(warehouse, item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(warehouse, item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
		final MarketingInfo mi = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item, final LocalDate when){
				return false;
			}
			@Override
			public Season season(LocalDate when) {
				return Season.Fall;
			};
		};
		final InventoryManager im = new AceInventoryManager(db, mi);
		final LocalDate today = LocalDate.now();

		//when
		final List<Order> actual = im.getOrders(today);

		//then
		assertEquals(1, actual.size());
		assertEquals(item, actual.get(0).item);
		assertEquals( 10, actual.get(0).quantity);
		assertEquals( Warehouse.Ashford, actual.get(0).warehouse);
    }
    
    @Test
	public void haveInOneWarehouseButNeedInAnother(){
    	//given
		final Integer onHand = 5;
		final Integer shouldHave = 15;
		final Integer onOrder = 0;
		final Warehouse warehouse = Warehouse.Ashford;
		final Schedule schedule = new AnyDay();
		final Item item = new StockedItem(shouldHave, schedule);
		final Table<Warehouse, Item, Integer> store = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(warehouse, item, onHand).build();
		final Table<Warehouse, Item, Integer> itemsOnOrder = new ImmutableTable.Builder<Warehouse, Item, Integer>().put(warehouse, item, onOrder).build();
		final InventoryDatabase db = new FakeDatabase(store, itemsOnOrder);
		final MarketingInfo mi = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item, final LocalDate when){
				return false;
			}
			@Override
			public Season season(LocalDate when) {
				return Season.Fall;
			};
		};
		final InventoryManager im = new AceInventoryManager(db, mi);
		final LocalDate today = LocalDate.now();

		//when
		final List<Order> actual = im.getOrders(today);

		//then
		assertEquals(1, actual.size());
		assertEquals(item, actual.get(0).item);
		assertEquals( 15, actual.get(0).quantity);
		assertEquals( Warehouse.home(), actual.get(0).warehouse);
    }
    
}
