package com.cjpowered.learn.marketing;

import java.time.LocalDate;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;

public interface MarketingInfo {
    /**
     * Fetch on-sale status.
     *
     * @param item
     *            item to query. Assumes item equality as defined by
     *            {@link InventoryDatabase}.
     * @param when
     *            the date to check
     *
     * @return fetched value
     */
    boolean onSale(Item item, LocalDate when);

    /**
     * Fetch the season
     *
     * @param when
     *            date to query
     *
     * @return fetched value
     */
    Season season(LocalDate when);

}
