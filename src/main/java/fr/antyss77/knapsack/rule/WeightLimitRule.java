package fr.antyss77.knapsack.rule;

import fr.antyss77.knapsack.core.Inventory;
import fr.antyss77.knapsack.core.InventoryRule;
import fr.antyss77.knapsack.core.ItemDefinition;

/**
 * Caps the total weight of an inventory. Weightless items are never blocked.
 */
public final class WeightLimitRule implements InventoryRule {

    private final double maxWeight;

    public WeightLimitRule(double maxWeight) {
        if (maxWeight < 0) {
            throw new IllegalArgumentException("maxWeight must be >= 0, got " + maxWeight);
        }
        this.maxWeight = maxWeight;
    }

    public double maxWeight() {
        return maxWeight;
    }

    @Override
    public int acceptableAmount(Inventory inventory, ItemDefinition item, int amount) {
        if (item.weight() <= 0) {
            return amount;
        }
        double free = maxWeight - inventory.totalWeight();
        if (free <= 0) {
            return 0;
        }
        return (int) Math.min(amount, Math.floor(free / item.weight()));
    }
}
