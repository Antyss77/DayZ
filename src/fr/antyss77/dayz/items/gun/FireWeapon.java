package fr.antyss77.dayz.items.gun;

import fr.antyss77.dayz.items.Weapon;

public class FireWeapon extends Weapon {

    public FireWeapon(String name, WeaponType type, int damage) {
        super(name, WeaponCategory.FIREARM, type, damage);
    }
}
