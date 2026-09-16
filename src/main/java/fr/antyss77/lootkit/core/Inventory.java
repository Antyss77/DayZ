package fr.antyss77.lootkit.core;

import java.util.List;
import java.util.Optional;

/**
 * A container of {@link ItemStack}s, addressed by slot.
 *
 * <p>Implementations decide how items are laid out (fixed slots, weight-only
 * bag, grid...). Callers only need the contract below. Nothing here assumes a
 * game: a warehouse, a shopping basket or a character backpack all fit.
 */
public interface Inventory extends Iterable<ItemStack> {

    /** Number of slots. */
    int slotCount();

    /** The stack at {@code index}, or empty if the slot is free. */
    Optional<ItemStack> slot(int index);

    /**
     * Overwrites a slot. Pass {@code null} to empty it. Use this to write back a
     * modified {@link ItemState} — spending a round, wearing down durability.
     */
    void set(int index, ItemStack stack);

    /**
     * Adds up to {@code amount} plain units, filling compatible stacks first.
     *
     * @return the number of units that could <em>not</em> be added; 0 means
     *         everything fit. Nothing is ever silently dropped.
     */
    int add(ItemDefinition item, int amount);

    /**
     * Adds an existing stack. A stateful stack needs a free slot of its own; a
     * plain one merges as usual.
     *
     * @return the number of units that could not be added
     */
    int add(ItemStack stack);

    /**
     * Removes up to {@code amount} units of the given item, plain units first.
     *
     * @return how many units were actually removed
     */
    int remove(ItemDefinition item, int amount);

    /** Finds the slot holding a given instance. */
    Optional<Integer> slotOf(InstanceId instanceId);

    /** Removes and returns one specific instance, if present. */
    Optional<ItemStack> removeInstance(InstanceId instanceId);

    /** How many units of this item the inventory currently holds. */
    int count(ItemDefinition item);

    default boolean contains(ItemDefinition item, int amount) {
        return count(item) >= amount;
    }

    /**
     * How many plain units of {@code item} could be added right now, without
     * modifying anything. Useful for UI previews and for validating a trade
     * before committing to it.
     */
    int acceptableAmount(ItemDefinition item, int amount);

    /** Every non-empty stack, in slot order. */
    List<ItemStack> contents();

    double totalWeight();

    boolean isEmpty();

    /** Removes everything. */
    void clear();
}
