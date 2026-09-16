package fr.antyss77.knapsack.rule;

import fr.antyss77.knapsack.core.Inventory;
import fr.antyss77.knapsack.core.InventoryRule;
import fr.antyss77.knapsack.core.ItemDefinition;

/**
 * Caps how many units of the same item an inventory may hold, regardless of
 * how many slots are free — "one parachute per player".
 */
public final class MaxQuantityRule implements InventoryRule {

    private final int maxPerItem;

    public MaxQuantityRule(int maxPerItem) {
        if (maxPerItem < 0) {
            throw new IllegalArgumentException("maxPerItem must be >= 0, got " + maxPerItem);
        }
        this.maxPerItem = maxPerItem;
    }

    @Override
    public int acceptableAmount(Inventory inventory, ItemDefinition item, int amount) {
        return Math.max(0, Math.min(amount, maxPerItem - inventory.count(item)));
    }
}
