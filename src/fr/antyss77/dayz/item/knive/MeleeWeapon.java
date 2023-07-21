package fr.antyss77.dayz.item.knive;


import fr.antyss77.dayz.item.Item;

public class MeleeWeapon extends Item {

    protected int durability;

    public MeleeWeapon(String name, ItemCategory category, ItemType type, int damage, int durability) {
        super(name, ItemCategory.MELEE_WEAPON, type, damage);
        this.durability = durability;
    }
}
