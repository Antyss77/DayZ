package fr.antyss77.dayz.item.gun;

import fr.antyss77.dayz.item.Item;

public class FireWeapon extends Item {

    public FireWeapon(String name, ItemCategory category,  ItemType type, int damage) {
        super(name, ItemCategory.FIREARM, type, damage);
    }
}
