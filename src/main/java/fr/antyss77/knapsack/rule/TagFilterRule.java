package fr.antyss77.knapsack.rule;

import fr.antyss77.knapsack.core.Inventory;
import fr.antyss77.knapsack.core.InventoryRule;
import fr.antyss77.knapsack.core.ItemDefinition;

import java.util.Set;

/**
 * Restricts an inventory to items carrying at least one of the given tags —
 * an ammo pouch that only takes {@code "ammo"}, a fridge that only takes
 * {@code "perishable"}, and so on.
 */
public final class TagFilterRule implements InventoryRule {

    private final Set<String> allowedTags;

    public TagFilterRule(String... allowedTags) {
        this(Set.of(allowedTags));
    }

    public TagFilterRule(Set<String> allowedTags) {
        this.allowedTags = Set.copyOf(allowedTags);
    }

    public Set<String> allowedTags() {
        return allowedTags;
    }

    @Override
    public int acceptableAmount(Inventory inventory, ItemDefinition item, int amount) {
        for (String tag : allowedTags) {
            if (item.hasTag(tag)) {
                return amount;
            }
        }
        return 0;
    }
}
