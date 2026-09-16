package fr.antyss77.lootkit.example.survival;

import fr.antyss77.lootkit.core.Inventory;
import fr.antyss77.lootkit.core.ItemDefinition;
import fr.antyss77.lootkit.core.ItemRegistry;
import fr.antyss77.lootkit.core.ItemStack;
import fr.antyss77.lootkit.core.SlotInventory;
import fr.antyss77.lootkit.effect.EffectTable;
import fr.antyss77.lootkit.rule.MaxQuantityRule;
import fr.antyss77.lootkit.rule.TagFilterRule;
import fr.antyss77.lootkit.rule.WeightLimitRule;

/**
 * Runnable tour of the library: {@code mvn -q compile exec:java} or run this
 * class from your IDE.
 */
public final class SurvivalDemo {

    public static void main(String[] args) {
        ItemRegistry registry = SurvivalItems.registry();

        Inventory backpack = SlotInventory.withSlots(8)
                .rule(new WeightLimitRule(25.0))
                .rule(new MaxQuantityRule(200))
                .build();

        // A weapon gets its own instance: it carries ammo, so it cannot stack.
        ItemStack rifle = Ammunition.newWeapon(SurvivalItems.AK47);
        backpack.add(rifle);
        backpack.add(SurvivalItems.AMMO_762X39, 120);
        backpack.add(SurvivalItems.BANDAGE, 7);
        backpack.add(SurvivalItems.MOLOTOV, 2);
        print("Backpack", backpack);

        section("1. Two kinds of id");
        System.out.println("  definition id : " + SurvivalItems.AK47.id() + "   <- typed in commands, stable forever");
        System.out.println("  instance id   : " + rifle.instanceId().orElseThrow() + "   <- generated, names this one rifle");
        System.out.println("  /give ak47    -> " + registry.resolve("AK47").orElseThrow().name());
        System.out.println("  /give ak4     -> unknown, did you mean " + registry.suggest("ak4", 3) + "?");

        int slot = backpack.slotOf(rifle.instanceId().orElseThrow()).orElseThrow();
        System.out.println("  found in slot " + slot + " via its instance id");

        section("2. Ammunition, matched by calibre");
        System.out.println("  AK-47 accepts  : " + names(Ammunition.compatibleAmmo(registry, SurvivalItems.AK47)));
        System.out.println("  7.62x39 feeds  : " + names(Ammunition.weaponsFor(registry, SurvivalItems.AMMO_762X39)));
        System.out.println("  Glock accepts  : " + names(Ammunition.compatibleAmmo(registry, SurvivalItems.GLOCK_42)));

        Ammunition.Reload reload = Ammunition.reload(backpack, slot);
        System.out.printf("  reloaded %d rounds of %s%n", reload.rounds(), reload.ammo().name());
        Ammunition.fire(backpack, slot);
        Ammunition.fire(backpack, slot);
        System.out.printf("  after two shots: %d/%d rounds, %d left in the bag%n",
                Ammunition.loadedRounds(backpack.slot(slot).orElseThrow()),
                Ammunition.magazineSize(SurvivalItems.AK47),
                backpack.count(SurvivalItems.AMMO_762X39));

        section("3. Effects, per item and per category");
        EffectTable effects = SurvivalEffects.table();
        for (ItemDefinition item : new ItemDefinition[]{
                SurvivalItems.AK47, SurvivalItems.PKM, SurvivalItems.KATANA,
                SurvivalItems.MOLOTOV, SurvivalItems.SMOKE_GRENADE}) {
            System.out.printf("  %-18s %s%n", item.name(), effects.of(item));
        }
        System.out.println("  (the PKM and the molotov override what their category grants them)");

        section("4. Rules");
        int leftover = backpack.add(registry.require("iron_armor"), 3);
        System.out.printf("  3 iron armours offered, %d refused by the weight limit%n", leftover);

        Inventory pouch = SlotInventory.withSlots(2).rule(new TagFilterRule("ammo")).build();
        System.out.println("  ammo pouch refuses the katana: leftover = " + pouch.add(SurvivalItems.KATANA, 1));

        System.out.println();
        print("Backpack", backpack);
    }

    private static String names(java.util.List<ItemDefinition> items) {
        return items.stream().map(ItemDefinition::name).reduce((a, b) -> a + ", " + b).orElse("none");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println(title);
    }

    private static void print(String title, Inventory inventory) {
        System.out.printf("%s - %d/%d slots used, %.2f kg%n",
                title, inventory.contents().size(), inventory.slotCount(), inventory.totalWeight());
        inventory.forEach(stack -> System.out.println("  " + stack));
    }

    private SurvivalDemo() {
    }
}
