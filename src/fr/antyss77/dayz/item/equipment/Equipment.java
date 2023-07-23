package fr.antyss77.dayz.item.equipment;

import fr.antyss77.dayz.item.Item;

public class Equipment extends Item {
    protected int durability;
    public Equipment(String name, ItemCategory category, ItemType type, int damage, int durability) {
        super(name, ItemCategory.EQUIPMENT, type, damage);
        this.durability = durability;
    }

}
