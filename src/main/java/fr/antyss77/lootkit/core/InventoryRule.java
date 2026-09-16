package fr.antyss77.lootkit.core;

/**
 * A constraint applied before items enter an inventory: a weight limit, an
 * allow-list of tags, a per-item cap, whatever your project needs.
 *
 * <p>Rules compose — an inventory applies all of them and keeps the smallest
 * acceptable amount — so new restrictions never require touching
 * {@link SlotInventory}.
 *
 * @see fr.antyss77.lootkit.rule.WeightLimitRule
 * @see fr.antyss77.lootkit.rule.TagFilterRule
 */
@FunctionalInterface
public interface InventoryRule {

    /**
     * @param inventory the inventory being added to, in its current state
     * @param item      the item being offered
     * @param amount    how many units the caller wants to add
     * @return how many of those units this rule allows, between 0 and {@code amount}
     */
    int acceptableAmount(Inventory inventory, ItemDefinition item, int amount);
}
