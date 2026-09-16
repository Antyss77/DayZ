package fr.antyss77.lootkit.core;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDefinitionTest {

    private static final Attribute<Integer> DAMAGE = Attribute.of("damage", Integer.class);

    private static final ItemDefinition KNIFE = ItemDefinition.builder("knife", "Knife")
            .weight(0.05)
            .tag("weapon", "tool")
            .attribute(DAMAGE, 12)
            .build();

    @Test
    void exposesTypedAttributes() {
        assertEquals(12, (int) KNIFE.attribute(DAMAGE).orElseThrow());
        assertEquals(0, (int) KNIFE.attributeOr(Attribute.of("armor", Integer.class), 0));
    }

    @Test
    void identityIsBasedOnIdAlone() {
        ItemDefinition rebalanced = KNIFE.toBuilder().attribute(DAMAGE, 20).build();

        assertEquals(KNIFE, rebalanced);
        assertEquals(20, (int) rebalanced.attributeOr(DAMAGE, 0));
        assertEquals(12, (int) KNIFE.attributeOr(DAMAGE, 0), "the original must stay untouched");
    }

    @Test
    void tagsAndAttributesAreImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> KNIFE.tags().add("armor"));
        assertTrue(KNIFE.hasTag("tool"));
        assertFalse(KNIFE.hasTag("armor"));
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> ItemDefinition.builder("", "Knife").build());
        assertThrows(IllegalArgumentException.class, () -> ItemDefinition.builder("a", "A").weight(-1).build());
        assertThrows(IllegalArgumentException.class, () -> ItemDefinition.builder("a", "A").maxStackSize(0).build());
    }

    @Test
    void stackRejectsAmountsOutsideItsBounds() {
        assertThrows(IllegalArgumentException.class, () -> ItemStack.of(KNIFE, 0));
        assertThrows(IllegalArgumentException.class, () -> ItemStack.of(KNIFE, 2));
    }

    @Test
    void stackReportsSpaceAndWeight() {
        ItemDefinition ammo = ItemDefinition.builder("ammo", "Ammo")
                .weight(0.01)
                .maxStackSize(30)
                .build();
        ItemStack stack = ItemStack.of(ammo, 12);

        assertEquals(18, stack.spaceLeft());
        assertFalse(stack.isFull());
        assertEquals(0.12, stack.totalWeight(), 1e-9);
        assertTrue(stack.withAmount(30).isFull());
    }

    @Test
    void registryLooksItemsUpByIdAndTag() {
        ItemRegistry registry = new ItemRegistry().register(KNIFE);

        assertEquals(KNIFE, registry.require("knife"));
        assertEquals(1, registry.withTag("weapon").size());
        assertTrue(registry.find("missing").isEmpty());
        assertThrows(NoSuchElementException.class, () -> registry.require("missing"));
        assertThrows(IllegalStateException.class, () -> registry.register(KNIFE));
    }
}
