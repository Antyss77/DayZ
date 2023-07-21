package fr.antyss77.dayz.items.knive;

import fr.antyss77.dayz.items.Weapon;


public class MeleeWeapon extends Weapon {

    protected int durability;

    public MeleeWeapon(String name, WeaponCategory category, WeaponType type, int damage, int durability) {
        super(name, WeaponCategory.MELEE_WEAPON, type, damage);
        this.durability = durability;
    }
}
