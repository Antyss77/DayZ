package fr.antyss77.dayz.item.projectiles;

import fr.antyss77.dayz.item.Item;

public class Projectiles extends Item {

    protected int durability;

    public Projectiles(String name, ItemCategory category, ItemType type, int damage, int durability) {
        super(name, ItemCategory.MELEE_WEAPON, type, damage);
        this.durability = durability;
    }
}
