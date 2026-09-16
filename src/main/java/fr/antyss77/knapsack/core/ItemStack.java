package fr.antyss77.knapsack.core;

import java.util.Objects;
import java.util.Optional;

/**
 * A quantity of one item, as held in a single slot. Immutable: every operation
 * returns a new stack, which keeps inventory code free of aliasing bugs.
 *
 * <p>A stack is either <em>plain</em> — interchangeable units that merge freely,
 * like 60 rounds of ammo — or <em>stateful</em>: a single unit carrying its own
 * {@link ItemState} and therefore its own {@link InstanceId}. Two stateful
 * stacks never merge, which is what keeps "the rifle with 12 rounds" distinct
 * from "the rifle with 30".
 *
 * @param item   what the stack holds
 * @param amount how many units, between 1 and {@code item.maxStackSize()};
 *               always 1 when a state is present
 * @param state  per-instance data, or {@code null} for a plain stack
 */
public record ItemStack(ItemDefinition item, int amount, ItemState state) {

    public ItemStack {
        Objects.requireNonNull(item, "item");
        if (amount < 1) {
            throw new IllegalArgumentException("amount must be >= 1, got " + amount);
        }
        if (amount > item.maxStackSize()) {
            throw new IllegalArgumentException(
                    "amount " + amount + " exceeds max stack size " + item.maxStackSize()
                            + " for " + item.id());
        }
        if (state != null && amount != 1) {
            throw new IllegalArgumentException(
                    "a stack with per-instance state holds exactly 1 unit, got " + amount);
        }
    }

    public static ItemStack of(ItemDefinition item) {
        return new ItemStack(item, 1, null);
    }

    public static ItemStack of(ItemDefinition item, int amount) {
        return new ItemStack(item, amount, null);
    }

    /** A single unit with a freshly generated instance id. */
    public static ItemStack unique(ItemDefinition item) {
        return new ItemStack(item, 1, ItemState.create());
    }

    public static ItemStack of(ItemDefinition item, ItemState state) {
        return new ItemStack(item, 1, Objects.requireNonNull(state, "state"));
    }

    public boolean hasState() {
        return state != null;
    }

    public Optional<ItemState> stateOptional() {
        return Optional.ofNullable(state);
    }

    public Optional<InstanceId> instanceId() {
        return state == null ? Optional.empty() : Optional.of(state.id());
    }

    public ItemStack withAmount(int newAmount) {
        return new ItemStack(item, newAmount, state);
    }

    public ItemStack withState(ItemState newState) {
        return new ItemStack(item, 1, newState);
    }

    /** How many more units this slot could hold. Always 0 for stateful stacks. */
    public int spaceLeft() {
        return hasState() ? 0 : item.maxStackSize() - amount;
    }

    public boolean isFull() {
        return spaceLeft() == 0;
    }

    public boolean holds(ItemDefinition other) {
        return item.equals(other);
    }

    /** True when plain units of {@code other} may be poured into this stack. */
    public boolean acceptsPlain(ItemDefinition other) {
        return !hasState() && holds(other) && !isFull();
    }

    public double totalWeight() {
        return item.weight() * amount;
    }

    @Override
    public String toString() {
        return hasState()
                ? item.name() + " #" + state.id().shortForm()
                : amount + "x " + item.name();
    }
}
