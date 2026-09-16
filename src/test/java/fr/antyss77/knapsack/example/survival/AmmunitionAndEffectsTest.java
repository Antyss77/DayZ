package fr.antyss77.knapsack.example.survival;

import fr.antyss77.knapsack.core.Inventory;
import fr.antyss77.knapsack.core.ItemRegistry;
import fr.antyss77.knapsack.core.ItemStack;
import fr.antyss77.knapsack.core.SlotInventory;
import fr.antyss77.knapsack.effect.Effect;
import fr.antyss77.knapsack.effect.EffectTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmmunitionAndEffectsTest {

    private final ItemRegistry registry = SurvivalItems.registry();

    @Test
    void weaponsAndAmmoMatchOnCalibre() {
        assertTrue(Ammunition.accepts(SurvivalItems.AK47, SurvivalItems.AMMO_762X39));
        assertFalse(Ammunition.accepts(SurvivalItems.AK47, SurvivalItems.AMMO_380ACP));
        assertFalse(Ammunition.accepts(SurvivalItems.KATANA, SurvivalItems.AMMO_762X39));
    }

    @Test
    void oneCalibreFeedsEveryWeaponThatDeclaresIt() {
        List<String> weapons = Ammunition.weaponsFor(registry, SurvivalItems.AMMO_762X39)
                .stream().map(item -> item.id()).toList();

        assertTrue(weapons.contains("ak47"));
        assertTrue(weapons.contains("pkm"));
        assertFalse(weapons.contains("glock42"));
    }

    @Test
    void reloadingConsumesRoundsFromTheInventory() {
        Inventory bag = SlotInventory.withSlots(6).build();
        bag.add(Ammunition.newWeapon(SurvivalItems.AK47));
        bag.add(SurvivalItems.AMMO_762X39, 50);

        Ammunition.Reload reload = Ammunition.reload(bag, 0);

        assertTrue(reload.happened());
        assertEquals(30, reload.rounds());
        assertEquals(SurvivalItems.AMMO_762X39, reload.ammo());
        assertEquals(20, bag.count(SurvivalItems.AMMO_762X39));
        assertEquals(30, Ammunition.loadedRounds(bag.slot(0).orElseThrow()));
    }

    @Test
    void reloadingTakesOnlyWhatIsAvailableAndRefusesWrongCalibres() {
        Inventory bag = SlotInventory.withSlots(6).build();
        bag.add(Ammunition.newWeapon(SurvivalItems.AK47));
        bag.add(SurvivalItems.AMMO_380ACP, 40);
        assertFalse(Ammunition.reload(bag, 0).happened());

        bag.add(SurvivalItems.AMMO_762X39, 7);
        assertEquals(7, Ammunition.reload(bag, 0).rounds());
        assertEquals(0, bag.count(SurvivalItems.AMMO_762X39));
    }

    @Test
    void firingSpendsRoundsAndStopsWhenEmpty() {
        Inventory bag = SlotInventory.withSlots(6).build();
        bag.add(Ammunition.newWeapon(SurvivalItems.GLOCK_42));
        bag.add(SurvivalItems.AMMO_380ACP, 2);
        Ammunition.reload(bag, 0);

        assertTrue(Ammunition.fire(bag, 0));
        assertTrue(Ammunition.fire(bag, 0));
        assertFalse(Ammunition.fire(bag, 0), "magazine is empty");
        assertEquals(0, Ammunition.loadedRounds(bag.slot(0).orElseThrow()));
    }

    @Test
    void reloadingKeepsTheSameWeaponInstance() {
        Inventory bag = SlotInventory.withSlots(6).build();
        ItemStack weapon = Ammunition.newWeapon(SurvivalItems.AK47);
        bag.add(weapon);
        bag.add(SurvivalItems.AMMO_762X39, 30);

        Ammunition.reload(bag, 0);

        assertEquals(weapon.instanceId(), bag.slot(0).orElseThrow().instanceId());
    }

    @Test
    void effectsAreInheritedFromTags() {
        EffectTable effects = SurvivalEffects.table();

        assertTrue(effects.has(SurvivalItems.AK47, "recoil"), "granted by the firearm tag");
        assertTrue(effects.has(SurvivalItems.KATANA, "bleed"));
        assertTrue(effects.has(SurvivalItems.SMOKE_GRENADE, "knockback"), "granted by the throwable tag");
        assertFalse(effects.has(SurvivalItems.BANDAGE, "recoil"));
    }

    @Test
    void itemLevelEffectsOverrideCategoryDefaults() {
        EffectTable effects = SurvivalEffects.table();

        SurvivalEffects.Recoil akRecoil = effects.of(SurvivalItems.AK47, SurvivalEffects.Recoil.class).orElseThrow();
        SurvivalEffects.Recoil pkmRecoil = effects.of(SurvivalItems.PKM, SurvivalEffects.Recoil.class).orElseThrow();
        assertEquals(1.0, akRecoil.strength(), 1e-9);
        assertEquals(2.5, pkmRecoil.strength(), 1e-9);

        SurvivalEffects.Burn burn = effects.of(SurvivalItems.MOLOTOV, SurvivalEffects.Burn.class).orElseThrow();
        assertEquals(7.0, burn.seconds(), 1e-9, "the molotov burns longer than a plain incendiary");
    }

    @Test
    void overridingDoesNotDuplicateAnEffect() {
        List<Effect> molotov = SurvivalEffects.table().of(SurvivalItems.MOLOTOV);

        assertEquals(1, molotov.stream().filter(effect -> effect.id().equals("burn")).count());
        assertEquals(2, molotov.size(), "knockback from the tag, burn overridden");
    }

    @Test
    void anItemWithoutEffectsResolvesToAnEmptyList() {
        assertTrue(SurvivalEffects.table().of(SurvivalItems.BANDAGE).isEmpty());
    }
}
