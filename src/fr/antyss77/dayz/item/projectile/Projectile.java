package fr.antyss77.dayz.item.projectile;

import fr.antyss77.dayz.item.Item;

public class Projectile extends Item {

    protected int durability;

    public Projectile(String name, ItemCategory category, ItemType type, int damage, int durability) {
        super(name, ItemCategory.EQUIPMENT, type, damage);
        this.durability = durability;
    }
}
