package fr.antyss77.knapsack.core;

import fr.antyss77.knapsack.rule.MaxQuantityRule;
import fr.antyss77.knapsack.rule.TagFilterRule;
import fr.antyss77.knapsack.rule.WeightLimitRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlotInventoryTest {

    private static final ItemDefinition BANDAGE = ItemDefinition.builder("bandage", "Bandage")
            .weight(0.5)
            .maxStackSize(10)
            .tag("medical")
            .build();

    private static final ItemDefinition RIFLE = ItemDefinition.builder("rifle", "Rifle")
            .weight(4.0)
            .tag("weapon")
            .build();

    @Test
    void addsUnitsAndReportsNoLeftoverWhenEverythingFits() {
        Inventory inventory = SlotInventory.withSlots(4).build();

        assertEquals(0, inventory.add(BANDAGE, 7));
        assertEquals(7, inventory.count(BANDAGE));
        assertEquals(1, inventory.contents().size());
    }

    @Test
    void fillsExistingStackBeforeOpeningANewSlot() {
        Inventory inventory = SlotInventory.withSlots(4).build();
        inventory.add(BANDAGE, 8);

        inventory.add(BANDAGE, 5);

        assertEquals(13, inventory.count(BANDAGE));
        assertEquals(2, inventory.contents().size());
        assertEquals(10, inventory.slot(0).orElseThrow().amount());
        assertEquals(3, inventory.slot(1).orElseThrow().amount());
    }

    @Test
    void returnsLeftoverWhenSlotsRunOut() {
        Inventory inventory = SlotInventory.withSlots(1).build();

        int leftover = inventory.add(BANDAGE, 14);

        assertEquals(4, leftover);
        assertEquals(10, inventory.count(BANDAGE));
    }

    @Test
    void addingIsAllOrNothingPerUnitButNeverLosesItems() {
        Inventory inventory = SlotInventory.withSlots(2).build();

        int leftover = inventory.add(RIFLE, 5);

        assertEquals(3, leftover);
        assertEquals(2, inventory.count(RIFLE));
    }

    @Test
    void removesUpToTheRequestedAmountAndReportsWhatWasTaken() {
        Inventory inventory = SlotInventory.withSlots(4).build();
        inventory.add(BANDAGE, 12);

        assertEquals(12, inventory.remove(BANDAGE, 20));
        assertEquals(0, inventory.count(BANDAGE));
        assertTrue(inventory.isEmpty());
    }

    @Test
    void removingFreesSlotsItNoLongerNeeds() {
        Inventory inventory = SlotInventory.withSlots(4).build();
        inventory.add(BANDAGE, 15);

        inventory.remove(BANDAGE, 5);

        assertEquals(10, inventory.count(BANDAGE));
        assertEquals(1, inventory.contents().size());
    }

    @Test
    void weightLimitCapsWhatCanBeAdded() {
        Inventory inventory = SlotInventory.withSlots(10)
                .rule(new WeightLimitRule(3.0))
                .build();

        int leftover = inventory.add(BANDAGE, 10); // 0.5 kg each, so 6 fit

        assertEquals(4, leftover);
        assertEquals(6, inventory.count(BANDAGE));
        assertEquals(3.0, inventory.totalWeight(), 1e-9);
    }

    @Test
    void tagFilterRejectsItemsWithoutAnAllowedTag() {
        Inventory pouch = SlotInventory.withSlots(4)
                .rule(new TagFilterRule("medical"))
                .build();

        assertEquals(1, pouch.add(RIFLE, 1));
        assertEquals(0, pouch.add(BANDAGE, 3));
        assertEquals(3, pouch.count(BANDAGE));
    }

    @Test
    void maxQuantityRuleCountsWhatIsAlreadyThere() {
        Inventory inventory = SlotInventory.withSlots(10)
                .rule(new MaxQuantityRule(12))
                .build();
        inventory.add(BANDAGE, 10);

        assertEquals(3, inventory.add(BANDAGE, 5));
        assertEquals(12, inventory.count(BANDAGE));
    }

    @Test
    void acceptableAmountPreviewsWithoutMutating() {
        Inventory inventory = SlotInventory.withSlots(1).build();

        assertEquals(10, inventory.acceptableAmount(BANDAGE, 25));
        assertTrue(inventory.isEmpty());
    }

    @Test
    void containsChecksQuantityNotJustPresence() {
        Inventory inventory = SlotInventory.withSlots(4).build();
        inventory.add(BANDAGE, 3);

        assertTrue(inventory.contains(BANDAGE, 3));
        assertFalse(inventory.contains(BANDAGE, 4));
    }

    @Test
    void rejectsInvalidSlotCountAndNegativeAmounts() {
        assertThrows(IllegalArgumentException.class, () -> SlotInventory.withSlots(0).build());

        Inventory inventory = SlotInventory.withSlots(2).build();
        assertThrows(IllegalArgumentException.class, () -> inventory.add(BANDAGE, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> inventory.slot(5));
    }

    @Test
    void clearEmptiesEverySlot() {
        Inventory inventory = SlotInventory.withSlots(4).build();
        inventory.add(BANDAGE, 12);

        inventory.clear();

        assertTrue(inventory.isEmpty());
        assertEquals(0.0, inventory.totalWeight(), 1e-9);
    }
}
