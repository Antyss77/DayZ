package fr.antyss77.lootkit.example.survival;

import fr.antyss77.lootkit.core.ItemDefinition;
import fr.antyss77.lootkit.core.ItemRegistry;

import static fr.antyss77.lootkit.example.survival.SurvivalAttributes.CALIBER;
import static fr.antyss77.lootkit.example.survival.SurvivalAttributes.ARMOR;
import static fr.antyss77.lootkit.example.survival.SurvivalAttributes.DAMAGE;
import static fr.antyss77.lootkit.example.survival.SurvivalAttributes.DURABILITY;
import static fr.antyss77.lootkit.example.survival.SurvivalAttributes.FUSE_SECONDS;
import static fr.antyss77.lootkit.example.survival.SurvivalAttributes.MAGAZINE_SIZE;

/**
 * A small catalogue for the sample survival game.
 *
 * <p>Note what is <em>not</em> here: no {@code AK47} class, no {@code Katana}
 * class. Adding a weapon is adding a few lines of data, which is also why the
 * same catalogue could just as well be loaded from JSON or a database.
 */
public final class SurvivalItems {

    public static final ItemDefinition AK47 = ItemDefinition.builder("ak47", "AK-47")
            .weight(3.6)
            .tag("weapon", "firearm", "assault_rifle")
            .attribute(DAMAGE, 35)
            .attribute(CALIBER, "762x39")
            .attribute(MAGAZINE_SIZE, 30)
            .build();

    public static final ItemDefinition GLOCK_42 = ItemDefinition.builder("glock42", "Glock 42")
            .weight(0.4)
            .tag("weapon", "firearm", "handgun")
            .attribute(DAMAGE, 18)
            .attribute(CALIBER, "380acp")
            .attribute(MAGAZINE_SIZE, 6)
            .build();

    public static final ItemDefinition AMMO_762X39 = ItemDefinition.builder("ammo_762x39", "7.62x39mm")
            .weight(0.016)
            .maxStackSize(60)
            .tag("ammo")
            .attribute(CALIBER, "762x39")
            .build();

    public static final ItemDefinition AMMO_380ACP = ItemDefinition.builder("ammo_380acp", ".380 ACP")
            .weight(0.009)
            .maxStackSize(60)
            .tag("ammo")
            .attribute(CALIBER, "380acp")
            .build();

    /** Shares a calibre with the AK-47: no weapon definition had to change. */
    public static final ItemDefinition PKM = ItemDefinition.builder("pkm", "PKM")
            .weight(7.5)
            .tag("weapon", "firearm", "machine_gun")
            .attribute(DAMAGE, 40)
            .attribute(CALIBER, "762x39")
            .attribute(MAGAZINE_SIZE, 100)
            .build();

    public static final ItemDefinition OPINEL = ItemDefinition.builder("opinel", "Opinel knife")
            .weight(0.05)
            .tag("weapon", "melee", "knife", "tool")
            .attribute(DAMAGE, 12)
            .attribute(DURABILITY, 90)
            .build();

    public static final ItemDefinition KATANA = ItemDefinition.builder("katana", "Katana")
            .weight(1.2)
            .tag("weapon", "melee", "sword")
            .attribute(DAMAGE, 48)
            .attribute(DURABILITY, 120)
            .build();

    public static final ItemDefinition IRON_ARMOR = ItemDefinition.builder("iron_armor", "Iron armour")
            .weight(9.0)
            .tag("equipment", "armor")
            .attribute(ARMOR, 30)
            .attribute(DURABILITY, 90)
            .build();

    public static final ItemDefinition DIAMOND_ARMOR = ItemDefinition.builder("diamond_armor", "Diamond armour")
            .weight(7.5)
            .tag("equipment", "armor")
            .attribute(ARMOR, 55)
            .attribute(DURABILITY, 240)
            .build();

    public static final ItemDefinition PARACHUTE = ItemDefinition.builder("parachute", "Parachute")
            .weight(8.0)
            .tag("equipment", "utility")
            .attribute(DURABILITY, 300)
            .build();

    public static final ItemDefinition MOLOTOV = ItemDefinition.builder("molotov", "Molotov cocktail")
            .weight(0.7)
            .maxStackSize(3)
            .tag("weapon", "throwable", "incendiary")
            .attribute(DAMAGE, 40)
            .attribute(FUSE_SECONDS, 2.5)
            .build();

    public static final ItemDefinition SMOKE_GRENADE = ItemDefinition.builder("smoke", "Smoke grenade")
            .weight(0.45)
            .maxStackSize(3)
            .tag("throwable")
            .attribute(FUSE_SECONDS, 1.5)
            .build();

    public static final ItemDefinition BANDAGE = ItemDefinition.builder("bandage", "Bandage")
            .weight(0.05)
            .maxStackSize(10)
            .tag("consumable", "medical")
            .build();

    /** A registry holding every item above, ready to use. */
    public static ItemRegistry registry() {
        return new ItemRegistry()
                .register(AK47)
                .register(GLOCK_42)
                .register(AMMO_762X39)
                .register(AMMO_380ACP)
                .register(PKM)
                .register(OPINEL)
                .register(KATANA)
                .register(IRON_ARMOR)
                .register(DIAMOND_ARMOR)
                .register(PARACHUTE)
                .register(MOLOTOV)
                .register(SMOKE_GRENADE)
                .register(BANDAGE);
    }

    private SurvivalItems() {
    }
}
