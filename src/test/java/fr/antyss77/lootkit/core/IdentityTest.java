package fr.antyss77.lootkit.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdentityTest {

    private static final Attribute<Integer> ROUNDS = Attribute.of("rounds", Integer.class);

    private static final ItemDefinition RIFLE = ItemDefinition.builder("ak47", "AK-47")
            .weight(3.6)
            .tag("firearm")
            .build();

    private static final ItemDefinition AMMO = ItemDefinition.builder("ammo_762x39", "7.62x39mm")
            .weight(0.016)
            .maxStackSize(60)
            .tag("ammo")
            .build();

    @Test
    void definitionIdsAreConstrainedSoTheyCanBeTypedInCommands() {
        assertThrows(IllegalArgumentException.class, () -> ItemDefinition.builder("AK 47", "x").build());
        assertThrows(IllegalArgumentException.class, () -> ItemDefinition.builder("ak/47", "x").build());
        assertEquals("mymod:ak47", ItemDefinition.builder("mymod:ak47", "x").build().id());
    }

    @Test
    void instanceIdsAreUniquePerItem() {
        ItemStack first = ItemStack.unique(RIFLE);
        ItemStack second = ItemStack.unique(RIFLE);

        assertNotEquals(first.instanceId().orElseThrow(), second.instanceId().orElseThrow());
        assertEquals(first.item(), second.item(), "same kind of item, different objects");
        assertEquals(8, first.instanceId().orElseThrow().shortForm().length());
    }

    @Test
    void statefulStacksHoldExactlyOneUnitAndNeverMerge() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemStack(AMMO, 2, ItemState.create()));

        Inventory inventory = SlotInventory.withSlots(4).build();
        inventory.add(ItemStack.unique(RIFLE));
        inventory.add(ItemStack.unique(RIFLE));

        assertEquals(2, inventory.contents().size());
        assertEquals(2, inventory.count(RIFLE));
    }

    @Test
    void stateSurvivesModificationUnderTheSameInstanceId() {
        ItemState state = ItemState.create().with(ROUNDS, 30);
        ItemState fired = state.with(ROUNDS, 29);

        assertEquals(state.id(), fired.id(), "still the same rifle");
        assertEquals(30, (int) state.getOr(ROUNDS, 0), "the original state is untouched");
        assertEquals(29, (int) fired.getOr(ROUNDS, 0));
        assertEquals(state, fired, "identity is the instance id, not the contents");
    }

    @Test
    void inventoryFindsAndRemovesASpecificInstance() {
        Inventory inventory = SlotInventory.withSlots(4).build();
        ItemStack tracked = ItemStack.unique(RIFLE);
        inventory.add(ItemStack.unique(RIFLE));
        inventory.add(tracked);
        InstanceId id = tracked.instanceId().orElseThrow();

        assertEquals(1, inventory.slotOf(id).orElseThrow());
        assertTrue(inventory.removeInstance(id).isPresent());
        assertEquals(1, inventory.count(RIFLE));
        assertTrue(inventory.slotOf(id).isEmpty());
    }

    @Test
    void plainUnitsAreConsumedBeforeTrackedOnes() {
        Inventory inventory = SlotInventory.withSlots(4).build();
        ItemStack tracked = ItemStack.unique(RIFLE);
        inventory.add(tracked);
        inventory.add(RIFLE, 1);

        inventory.remove(RIFLE, 1);

        assertTrue(inventory.slotOf(tracked.instanceId().orElseThrow()).isPresent());
    }

    @Test
    void instanceIdsRoundTripThroughText() {
        InstanceId id = InstanceId.random();

        assertEquals(id, InstanceId.parse(id.toString()));
    }

    @Test
    void registryResolvesCommandInputAndSuggestsAlternatives() {
        ItemRegistry registry = new ItemRegistry()
                .register(RIFLE)
                .register(AMMO)
                .register(ItemDefinition.builder("mymod:ak74", "AK-74").build());

        assertEquals(RIFLE, registry.resolve("AK47").orElseThrow());
        assertEquals("mymod:ak74", registry.resolve("ak74").orElseThrow().id(), "namespace may be omitted");
        assertTrue(registry.resolve("rocket").isEmpty());
        assertTrue(registry.suggest("ak", 5).contains("ak47"));
        assertFalse(registry.suggest("ak", 5).contains("ammo_762x39"));
    }
}
