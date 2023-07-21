package fr.antyss77.dayz.item;

public class Item {
    public String name;
    protected ItemCategory category;
    protected ItemType type;
    protected int damage;



    public Item(String name, ItemCategory category, ItemType type, int damage) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.damage = damage;
    }

    public enum ItemCategory {
        FIREARM,
        MELEE_WEAPON,
        EQUIPMENT,
        PROJECTILES
    }

    public enum ItemType {
        ASSAULT_RIFLE,
        SUB_MACHINE_GUN,
        PUMP_ACTION_RIFLE,
        TACTICAL_RIFLE,
        PRECISION_RIFLE,
        MACHINE_GUN,
        HANDGUN,
        LAUNCHER,
        SWORD,
        AXE,
        KNIFE,
        ARMOR,
        UTILITIES,
        GRENADE

    }

    public void loadItem() {
        System.out.println("Name: " + name);
        System.out.println("Category: " + category);
        System.out.println("Type: " + type);
        System.out.println("Damage: " + damage);
    }
}
