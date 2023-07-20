package fr.antyss77.dayz.weapon.knive;

import fr.antyss77.dayz.weapon.Weapon;

public class MeleeWeapon extends Weapon {
    public MeleeWeapon(String name, WeaponCategory category, WeaponType type, int damage) {
        super(name, WeaponCategory.MELEE_WEAPON, WeaponType.SWORD, damage);
    }
}
