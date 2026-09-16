package fr.antyss77.knapsack.example.survival;

import fr.antyss77.knapsack.core.ItemDefinition;
import fr.antyss77.knapsack.core.ItemRegistry;
import fr.antyss77.knapsack.effect.EffectTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.EQUIPMENT_SLOT;

class ExpandedCatalogueTest {

    private final ItemRegistry registry = SurvivalItems.registry();

    @Test
    void everyNewFirearmHasExactlyOneMatchingAmmunition() {
        for (ItemDefinition weapon : new ItemDefinition[]{
                SurvivalItems.M4A1, SurvivalItems.MP5, SurvivalItems.KAR98K,
                SurvivalItems.REMINGTON_870, SurvivalItems.DESERT_EAGLE}) {
            List<ItemDefinition> ammo = Ammunition.compatibleAmmo(registry, weapon);
            assertEquals(1, ammo.size(), weapon.name() + " should accept exactly one calibre");
        }
    }

    @Test
    void ak47AndPkmShareTheSameCalibreButNotTheSameAmmoAsTheRest() {
        List<ItemDefinition> feeders = Ammunition.weaponsFor(registry, SurvivalItems.AMMO_762X39);

        assertTrue(feeders.contains(SurvivalItems.AK47));
        assertTrue(feeders.contains(SurvivalItems.PKM));
        assertEquals(2, feeders.size());
        assertFalse(Ammunition.accepts(SurvivalItems.M4A1, SurvivalItems.AMMO_762X39));
    }

    @Test
    void equipmentDeclaresExactlyOneSlot() {
        for (ItemDefinition gear : new ItemDefinition[]{
                SurvivalItems.LIGHT_HELMET, SurvivalItems.HEAVY_HELMET, SurvivalItems.TACTICAL_GLOVES,
                SurvivalItems.LIGHT_VEST, SurvivalItems.HEAVY_VEST, SurvivalItems.RIOT_SHIELD}) {
            assertTrue(gear.attribute(EQUIPMENT_SLOT).isPresent(), gear.name() + " must declare a slot");
        }
        assertEquals("head", SurvivalItems.LIGHT_HELMET.attributeOr(EQUIPMENT_SLOT, ""));
        assertEquals("chest", SurvivalItems.HEAVY_VEST.attributeOr(EQUIPMENT_SLOT, ""));
        assertEquals("offhand", SurvivalItems.RIOT_SHIELD.attributeOr(EQUIPMENT_SLOT, ""));
    }

    @Test
    void headAndChestSlotsHaveTwoTiersEach() {
        assertEquals(2, registry.withTag("headwear").size(), "light and heavy helmet");
        assertEquals(2, registry.withTag("vest").size(), "light and heavy vest");
        assertEquals(1, registry.withTag("shield").size());
    }

    @Test
    void meleeWeaponsCoverFourFamilies() {
        assertTrue(SurvivalItems.TOMAHAWK.hasTag("axe"));
        assertTrue(SurvivalItems.COMBAT_KNIFE.hasTag("knife"));
        assertTrue(SurvivalItems.SWISS_ARMY_KNIFE.hasTag("knife"));
        assertTrue(SurvivalItems.KATANA.hasTag("sword"));
        assertEquals(3, registry.withTag("knife").size(), "opinel, combat knife, swiss knife");
    }

    @Test
    void explosiveTagGrantsAnExplosionThatFragOverrides() {
        EffectTable effects = SurvivalEffects.table();

        SurvivalEffects.Explosion frag =
                effects.of(SurvivalItems.FRAG_GRENADE, SurvivalEffects.Explosion.class).orElseThrow();
        assertEquals(75.0, frag.damage(), 1e-9, "the grenade overrides the generic explosive default");
    }

    @Test
    void flashbangGetsABlindEffectFromItsOwnTag() {
        EffectTable effects = SurvivalEffects.table();

        assertTrue(effects.has(SurvivalItems.FLASHBANG, "blind"));
        assertTrue(effects.has(SurvivalItems.FLASHBANG, "knockback"), "still a throwable");
    }

    @Test
    void newConsumablesAreTaggedMedicalLikeTheBandage() {
        assertTrue(SurvivalItems.MORPHINE.hasTag("medical"));
        assertTrue(SurvivalItems.SYRINGE.hasTag("medical"));
        assertEquals(3, registry.withTag("medical").size());
    }

    @Test
    void catalogueHasNoDuplicateIds() {
        assertEquals(registry.size(), registry.all().stream().map(ItemDefinition::id).distinct().count());
    }
}
