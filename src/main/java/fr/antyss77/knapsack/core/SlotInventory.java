package fr.antyss77.knapsack.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The default {@link Inventory}: a fixed number of slots, automatic stacking,
 * and a list of {@link InventoryRule}s.
 *
 * <pre>{@code
 * Inventory backpack = SlotInventory.withSlots(12)
 *         .rule(new WeightLimitRule(25.0))
 *         .build();
 *
 * int leftover = backpack.add(bandage, 10);
 * }</pre>
 *
 * <p>Not thread-safe; guard it externally if several threads share a container.
 */
public final class SlotInventory implements Inventory {

    private final ItemStack[] slots;
    private final List<InventoryRule> rules;

    private SlotInventory(int slotCount, List<InventoryRule> rules) {
        if (slotCount < 1) {
            throw new IllegalArgumentException("slotCount must be >= 1, got " + slotCount);
        }
        this.slots = new ItemStack[slotCount];
        this.rules = List.copyOf(rules);
    }

    public static Builder withSlots(int slotCount) {
        return new Builder(slotCount);
    }

    @Override
    public int slotCount() {
        return slots.length;
    }

    @Override
    public Optional<ItemStack> slot(int index) {
        Objects.checkIndex(index, slots.length);
        return Optional.ofNullable(slots[index]);
    }

    @Override
    public void set(int index, ItemStack stack) {
        Objects.checkIndex(index, slots.length);
        slots[index] = stack;
    }

    @Override
    public int acceptableAmount(ItemDefinition item, int amount) {
        Objects.requireNonNull(item, "item");
        if (amount <= 0) {
            return 0;
        }
        int allowed = Math.min(amount, freeCapacityFor(item));
        for (InventoryRule rule : rules) {
            allowed = Math.min(allowed, rule.acceptableAmount(this, item, allowed));
            if (allowed <= 0) {
                return 0;
            }
        }
        return allowed;
    }

    @Override
    public int add(ItemDefinition item, int amount) {
        Objects.requireNonNull(item, "item");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0, got " + amount);
        }
        int toAdd = acceptableAmount(item, amount);
        int remaining = toAdd;

        // Top up existing stacks before opening a new slot.
        for (int i = 0; i < slots.length && remaining > 0; i++) {
            ItemStack stack = slots[i];
            if (stack != null && stack.acceptsPlain(item)) {
                int moved = Math.min(remaining, stack.spaceLeft());
                slots[i] = stack.withAmount(stack.amount() + moved);
                remaining -= moved;
            }
        }
        for (int i = 0; i < slots.length && remaining > 0; i++) {
            if (slots[i] == null) {
                int moved = Math.min(remaining, item.maxStackSize());
                slots[i] = ItemStack.of(item, moved);
                remaining -= moved;
            }
        }
        return amount - (toAdd - remaining);
    }

    @Override
    public int add(ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        if (!stack.hasState()) {
            return add(stack.item(), stack.amount());
        }
        if (acceptableAmount(stack.item(), 1) < 1) {
            return 1;
        }
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                slots[i] = stack;
                return 0;
            }
        }
        return 1;
    }

    @Override
    public int remove(ItemDefinition item, int amount) {
        Objects.requireNonNull(item, "item");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0, got " + amount);
        }
        int remaining = amount;
        // Plain stacks first: never destroy a tracked instance when a generic
        // one would do. Then drain from the end so the inventory stays compact.
        remaining = drain(item, remaining, false);
        remaining = drain(item, remaining, true);
        return amount - remaining;
    }

    private int drain(ItemDefinition item, int remaining, boolean stateful) {
        for (int i = slots.length - 1; i >= 0 && remaining > 0; i--) {
            ItemStack stack = slots[i];
            if (stack == null || !stack.holds(item) || stack.hasState() != stateful) {
                continue;
            }
            int taken = Math.min(remaining, stack.amount());
            remaining -= taken;
            slots[i] = taken == stack.amount() ? null : stack.withAmount(stack.amount() - taken);
        }
        return remaining;
    }

    @Override
    public Optional<Integer> slotOf(InstanceId instanceId) {
        Objects.requireNonNull(instanceId, "instanceId");
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = slots[i];
            if (stack != null && stack.instanceId().filter(instanceId::equals).isPresent()) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> removeInstance(InstanceId instanceId) {
        return slotOf(instanceId).map(index -> {
            ItemStack stack = slots[index];
            slots[index] = null;
            return stack;
        });
    }

    @Override
    public int count(ItemDefinition item) {
        int total = 0;
        for (ItemStack stack : slots) {
            if (stack != null && stack.holds(item)) {
                total += stack.amount();
            }
        }
        return total;
    }

    @Override
    public List<ItemStack> contents() {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : slots) {
            if (stack != null) {
                result.add(stack);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public double totalWeight() {
        double total = 0;
        for (ItemStack stack : slots) {
            if (stack != null) {
                total += stack.totalWeight();
            }
        }
        return total;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : slots) {
            if (stack != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        Arrays.fill(slots, null);
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return contents().iterator();
    }

    @Override
    public String toString() {
        return "SlotInventory" + contents();
    }

    /** Plain units of {@code item} that would fit if no rule applied. */
    private int freeCapacityFor(ItemDefinition item) {
        int capacity = 0;
        for (ItemStack stack : slots) {
            if (stack == null) {
                capacity += item.maxStackSize();
            } else if (stack.acceptsPlain(item)) {
                capacity += stack.spaceLeft();
            }
        }
        return capacity;
    }

    public static final class Builder {

        private final int slotCount;
        private final List<InventoryRule> rules = new ArrayList<>();

        private Builder(int slotCount) {
            this.slotCount = slotCount;
        }

        public Builder rule(InventoryRule rule) {
            rules.add(Objects.requireNonNull(rule, "rule"));
            return this;
        }

        public SlotInventory build() {
            return new SlotInventory(slotCount, rules);
        }
    }
}
