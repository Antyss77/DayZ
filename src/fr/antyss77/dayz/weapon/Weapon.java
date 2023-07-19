package fr.antyss77.dayz.weapon;

public class Weapon {
    public String name;
    protected WeaponCategory category;
    protected WeaponType type;
    protected int damage;

    public Weapon(String name, WeaponCategory category, WeaponType type, int damage) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.damage = damage;
    }

    public enum WeaponCategory {
        FIREARM,
        MELEE_WEAPON
    }

    public enum WeaponType {
        ASSAULT_RIFLE,
        SUB_MACHINE_GUN,
        PUMP_ACTION_RIFLES,
        TACTICAL_RIFLES,
        PRECISION_RIFLES,
        HANDGUNS,
        LAUNCHERS,
        SWORD,
        AXE,
        KNIFE,
    }
}
