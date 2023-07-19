package fr.antyss77.dayz.weapon.gun;

import fr.antyss77.dayz.weapon.Weapon;

public class FireWeapon extends Weapon {
    public FireWeapon(String name, WeaponType type, int damage) {
        super(name, WeaponCategory.FIREARM, type, damage);
    }
}
