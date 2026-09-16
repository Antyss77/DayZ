package fr.antyss77.knapsack.example.survival;

import fr.antyss77.knapsack.core.ItemDefinition;
import fr.antyss77.knapsack.core.ItemRegistry;

import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.ARMOR;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.CALIBER;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.DAMAGE;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.DURABILITY;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.EQUIPMENT_SLOT;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.FUSE_SECONDS;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.MAGAZINE_SIZE;

/**
 * A small catalogue for the sample survival game.
 *
 * <p>Note what is <em>not</em> here: no {@code AK47} class, no {@code Katana}
 * class. Adding a weapon is adding a few lines of data, which is also why the
 * same catalogue could just as well be loaded from JSON or a database.
 *
 * <p>Grouped below by kind, purely for readability — nothing in the library
 * cares about the grouping, only about the tags each item carries.
 */
public final class SurvivalItems {

    // ---------------------------------------------------------------
    // Firearms
    // ---------------------------------------------------------------

    public static final ItemDefinition AK47 = ItemDefinition.builder("ak47", "AK-47")
            .weight(3.6)
            .tag("weapon", "firearm", "assault_rifle")
            .attribute(DAMAGE, 35)
            .attribute(CALIBER, "762x39")
            .attribute(MAGAZINE_SIZE, 30)
            .build();

    public static final ItemDefinition M4A1 = ItemDefinition.builder("m4a1", "M4A1")
            .weight(3.4)
            .tag("weapon", "firearm", "assault_rifle")
            .attribute(DAMAGE, 33)
            .attribute(CALIBER, "556x45")
            .attribute(MAGAZINE_SIZE, 30)
            .build();

    public static final ItemDefinition GLOCK_42 = ItemDefinition.builder("glock42", "Glock 42")
            .weight(0.4)
            .tag("weapon", "firearm", "handgun")
            .attribute(DAMAGE, 18)
            .attribute(CALIBER, "380acp")
            .attribute(MAGAZINE_SIZE, 6)
            .build();

    public static final ItemDefinition DESERT_EAGLE = ItemDefinition.builder("deagle", "Desert Eagle")
            .weight(1.1)
            .tag("weapon", "firearm", "handgun")
            .attribute(DAMAGE, 32)
            .attribute(CALIBER, "50ae")
            .attribute(MAGAZINE_SIZE, 7)
            .build();

    public static final ItemDefinition MP5 = ItemDefinition.builder("mp5", "MP5")
            .weight(2.5)
            .tag("weapon", "firearm", "submachine_gun")
            .attribute(DAMAGE, 22)
            .attribute(CALIBER, "9x19")
            .attribute(MAGAZINE_SIZE, 30)
            .build();

    public static final ItemDefinition KAR98K = ItemDefinition.builder("kar98k", "Kar98k")
            .weight(4.1)
            .tag("weapon", "firearm", "sniper_rifle")
            .attribute(DAMAGE, 65)
            .attribute(CALIBER, "792x57")
            .attribute(MAGAZINE_SIZE, 5)
            .build();

    public static final ItemDefinition REMINGTON_870 = ItemDefinition.builder("rem870", "Remington 870")
            .weight(3.2)
            .tag("weapon", "firearm", "shotgun")
            .attribute(DAMAGE, 55)
            .attribute(CALIBER, "12gauge")
            .attribute(MAGAZINE_SIZE, 8)
            .build();

    /** Shares a calibre with the AK-47: no weapon definition had to change. */
    public static final ItemDefinition PKM = ItemDefinition.builder("pkm", "PKM")
            .weight(7.5)
            .tag("weapon", "firearm", "machine_gun")
            .attribute(DAMAGE, 40)
            .attribute(CALIBER, "762x39")
            .attribute(MAGAZINE_SIZE, 100)
            .build();

    // ---------------------------------------------------------------
    // Ammunition — one entry per calibre used above
    // ---------------------------------------------------------------

    public static final ItemDefinition AMMO_762X39 = ItemDefinition.builder("ammo_762x39", "7.62x39mm")
            .weight(0.016)
            .maxStackSize(60)
            .tag("ammo")
            .attribute(CALIBER, "762x39")
            .build();

    public static final ItemDefinition AMMO_556X45 = ItemDefinition.builder("ammo_556x45", "5.56x45mm")
            .weight(0.012)
            .maxStackSize(60)
            .tag("ammo")
            .attribute(CALIBER, "556x45")
            .build();

    public static final ItemDefinition AMMO_380ACP = ItemDefinition.builder("ammo_380acp", ".380 ACP")
            .weight(0.009)
            .maxStackSize(60)
            .tag("ammo")
            .attribute(CALIBER, "380acp")
            .build();

    public static final ItemDefinition AMMO_50AE = ItemDefinition.builder("ammo_50ae", ".50 AE")
            .weight(0.023)
            .maxStackSize(40)
            .tag("ammo")
            .attribute(CALIBER, "50ae")
            .build();

    public static final ItemDefinition AMMO_9X19 = ItemDefinition.builder("ammo_9x19", "9x19mm")
            .weight(0.008)
            .maxStackSize(60)
            .tag("ammo")
            .attribute(CALIBER, "9x19")
            .build();

    public static final ItemDefinition AMMO_792X57 = ItemDefinition.builder("ammo_792x57", "7.92x57mm")
            .weight(0.027)
            .maxStackSize(30)
            .tag("ammo")
            .attribute(CALIBER, "792x57")
            .build();

    public static final ItemDefinition AMMO_12GAUGE = ItemDefinition.builder("ammo_12gauge", "12 gauge shell")
            .weight(0.032)
            .maxStackSize(30)
            .tag("ammo")
            .attribute(CALIBER, "12gauge")
            .build();

    // ---------------------------------------------------------------
    // Melee
    // ---------------------------------------------------------------

    public static final ItemDefinition OPINEL = ItemDefinition.builder("opinel", "Opinel knife")
            .weight(0.05)
            .tag("weapon", "melee", "knife", "tool")
            .attribute(DAMAGE, 12)
            .attribute(DURABILITY, 90)
            .build();

    public static final ItemDefinition COMBAT_KNIFE = ItemDefinition.builder("combat_knife", "Combat knife")
            .weight(0.3)
            .tag("weapon", "melee", "knife")
            .attribute(DAMAGE, 20)
            .attribute(DURABILITY, 130)
            .build();

    public static final ItemDefinition SWISS_ARMY_KNIFE = ItemDefinition.builder("swiss_knife", "Swiss army knife")
            .weight(0.12)
            .tag("weapon", "melee", "knife", "tool")
            .attribute(DAMAGE, 8)
            .attribute(DURABILITY, 150)
            .build();

    public static final ItemDefinition TOMAHAWK = ItemDefinition.builder("tomahawk", "Tomahawk")
            .weight(0.9)
            .tag("weapon", "melee", "axe")
            .attribute(DAMAGE, 32)
            .attribute(DURABILITY, 100)
            .build();

    public static final ItemDefinition KATANA = ItemDefinition.builder("katana", "Katana")
            .weight(1.2)
            .tag("weapon", "melee", "sword")
            .attribute(DAMAGE, 48)
            .attribute(DURABILITY, 120)
            .build();

    // ---------------------------------------------------------------
    // Tactical equipment — one slot each, no two pieces compete for a slot
    // ---------------------------------------------------------------

    public static final ItemDefinition LIGHT_HELMET = ItemDefinition.builder("light_helmet", "Light helmet")
            .weight(0.9)
            .tag("equipment", "armor", "headwear")
            .attribute(EQUIPMENT_SLOT, "head")
            .attribute(ARMOR, 15)
            .attribute(DURABILITY, 60)
            .build();

    public static final ItemDefinition HEAVY_HELMET = ItemDefinition.builder("heavy_helmet", "Heavy ballistic helmet")
            .weight(1.8)
            .tag("equipment", "armor", "headwear")
            .attribute(EQUIPMENT_SLOT, "head")
            .attribute(ARMOR, 35)
            .attribute(DURABILITY, 100)
            .build();

    public static final ItemDefinition TACTICAL_GLOVES = ItemDefinition.builder("tactical_gloves", "Tactical gloves")
            .weight(0.3)
            .tag("equipment", "armor")
            .attribute(EQUIPMENT_SLOT, "hands")
            .attribute(ARMOR, 5)
            .attribute(DURABILITY, 80)
            .build();

    public static final ItemDefinition LIGHT_VEST = ItemDefinition.builder("light_vest", "Light bulletproof vest")
            .weight(3.5)
            .tag("equipment", "armor", "vest")
            .attribute(EQUIPMENT_SLOT, "chest")
            .attribute(ARMOR, 25)
            .attribute(DURABILITY, 90)
            .build();

    public static final ItemDefinition HEAVY_VEST = ItemDefinition.builder("heavy_vest", "Heavy bulletproof vest")
            .weight(7.0)
            .tag("equipment", "armor", "vest")
            .attribute(EQUIPMENT_SLOT, "chest")
            .attribute(ARMOR, 50)
            .attribute(DURABILITY, 150)
            .build();

    public static final ItemDefinition RIOT_SHIELD = ItemDefinition.builder("riot_shield", "Riot shield")
            .weight(6.0)
            .tag("equipment", "armor", "shield")
            .attribute(EQUIPMENT_SLOT, "offhand")
            .attribute(ARMOR, 20)
            .attribute(DURABILITY, 200)
            .build();

    public static final ItemDefinition PARACHUTE = ItemDefinition.builder("parachute", "Parachute")
            .weight(8.0)
            .tag("equipment", "utility")
            .attribute(DURABILITY, 300)
            .build();

    // ---------------------------------------------------------------
    // Throwables
    // ---------------------------------------------------------------

    public static final ItemDefinition MOLOTOV = ItemDefinition.builder("molotov", "Molotov cocktail")
            .weight(0.7)
            .maxStackSize(3)
            .tag("weapon", "throwable", "incendiary")
            .attribute(DAMAGE, 40)
            .attribute(FUSE_SECONDS, 2.5)
            .build();

    public static final ItemDefinition FRAG_GRENADE = ItemDefinition.builder("frag_grenade", "Fragmentation grenade")
            .weight(0.5)
            .maxStackSize(3)
            .tag("weapon", "throwable", "explosive")
            .attribute(DAMAGE, 70)
            .attribute(FUSE_SECONDS, 3.5)
            .build();

    public static final ItemDefinition FLASHBANG = ItemDefinition.builder("flashbang", "Flashbang")
            .weight(0.35)
            .maxStackSize(3)
            .tag("throwable", "flashbang")
            .attribute(FUSE_SECONDS, 1.5)
            .build();

    public static final ItemDefinition SMOKE_GRENADE = ItemDefinition.builder("smoke", "Smoke grenade")
            .weight(0.45)
            .maxStackSize(3)
            .tag("throwable")
            .attribute(FUSE_SECONDS, 1.5)
            .build();

    // ---------------------------------------------------------------
    // Consumables
    // ---------------------------------------------------------------

    public static final ItemDefinition BANDAGE = ItemDefinition.builder("bandage", "Bandage")
            .weight(0.05)
            .maxStackSize(10)
            .tag("consumable", "medical")
            .build();

    public static final ItemDefinition MORPHINE = ItemDefinition.builder("morphine", "Morphine injector")
            .weight(0.03)
            .maxStackSize(5)
            .tag("consumable", "medical")
            .build();

    public static final ItemDefinition SYRINGE = ItemDefinition.builder("syringe", "Syringe")
            .weight(0.02)
            .maxStackSize(5)
            .tag("consumable", "medical")
            .build();

    /** A registry holding every item above, ready to use. */
    public static ItemRegistry registry() {
        return new ItemRegistry()
                // firearms
                .register(AK47)
                .register(M4A1)
                .register(GLOCK_42)
                .register(DESERT_EAGLE)
                .register(MP5)
                .register(KAR98K)
                .register(REMINGTON_870)
                .register(PKM)
                // ammunition
                .register(AMMO_762X39)
                .register(AMMO_556X45)
                .register(AMMO_380ACP)
                .register(AMMO_50AE)
                .register(AMMO_9X19)
                .register(AMMO_792X57)
                .register(AMMO_12GAUGE)
                // melee
                .register(OPINEL)
                .register(COMBAT_KNIFE)
                .register(SWISS_ARMY_KNIFE)
                .register(TOMAHAWK)
                .register(KATANA)
                // tactical equipment
                .register(LIGHT_HELMET)
                .register(HEAVY_HELMET)
                .register(TACTICAL_GLOVES)
                .register(LIGHT_VEST)
                .register(HEAVY_VEST)
                .register(RIOT_SHIELD)
                .register(PARACHUTE)
                // throwables
                .register(MOLOTOV)
                .register(FRAG_GRENADE)
                .register(FLASHBANG)
                .register(SMOKE_GRENADE)
                // consumables
                .register(BANDAGE)
                .register(MORPHINE)
                .register(SYRINGE);
    }

    private SurvivalItems() {
    }
}
