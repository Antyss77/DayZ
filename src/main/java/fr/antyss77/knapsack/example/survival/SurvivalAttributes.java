package fr.antyss77.knapsack.example.survival;

import fr.antyss77.knapsack.core.Attribute;

/**
 * Attribute keys for the sample survival game. They live in the example on
 * purpose: the core library has no opinion on what an item "does".
 *
 * <p>Copy this file into your own project and replace the keys with whatever
 * your domain needs — {@code EXPIRY_DATE} and {@code SKU} for a warehouse,
 * {@code MANA_COST} for a card game.
 */
public final class SurvivalAttributes {

    /** Damage per hit or per shot. */
    public static final Attribute<Integer> DAMAGE = Attribute.of("damage", Integer.class);

    /** Remaining uses before the item breaks. */
    public static final Attribute<Integer> DURABILITY = Attribute.of("durability", Integer.class);

    /** Damage absorbed when the item is worn. */
    public static final Attribute<Integer> ARMOR = Attribute.of("armor", Integer.class);

    /**
     * Body part an armour piece occupies: {@code "head"}, {@code "hands"},
     * {@code "chest"} or {@code "offhand"} for a carried shield. Two pieces
     * sharing a slot cannot both be worn — that check belongs to your
     * equip system, this attribute only carries the fact.
     */
    public static final Attribute<String> EQUIPMENT_SLOT = Attribute.of("equipmentSlot", String.class);

    /**
     * Calibre, carried by both weapons and ammunition. Matching calibres is what
     * links the two, so a new cartridge works with every existing weapon of that
     * calibre without editing a single weapon definition.
     */
    public static final Attribute<String> CALIBER = Attribute.of("caliber", String.class);

    /** Rounds held by a magazine or a loaded weapon. */
    public static final Attribute<Integer> MAGAZINE_SIZE = Attribute.of("magazineSize", Integer.class);

    /**
     * Rounds currently chambered. Per-instance state, not a definition
     * attribute: it describes one rifle, not every rifle.
     */
    public static final Attribute<Integer> LOADED_ROUNDS = Attribute.of("loadedRounds", Integer.class);

    /** Uses left on this particular item. Per-instance state. */
    public static final Attribute<Integer> WEAR = Attribute.of("wear", Integer.class);

    /** Seconds before a thrown item detonates. */
    public static final Attribute<Double> FUSE_SECONDS = Attribute.of("fuseSeconds", Double.class);

    private SurvivalAttributes() {
    }
}
