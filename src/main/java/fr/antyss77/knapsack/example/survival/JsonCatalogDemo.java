package fr.antyss77.knapsack.example.survival;

import fr.antyss77.knapsack.core.ItemDefinition;
import fr.antyss77.knapsack.core.ItemRegistry;
import fr.antyss77.knapsack.io.CatalogLoader;

import java.util.List;

/**
 * Loads the exact same catalogue as {@link SurvivalItems}, but from a JSON
 * file instead of Java code: {@code src/main/resources/catalog/survival-items.json}.
 *
 * <p>Run with {@code mvn -q compile exec:java -Dexec.mainClass=fr.antyss77.knapsack.example.survival.JsonCatalogDemo}.
 * The point is not that this catalogue differs from the hard-coded one — it is
 * that it does not: everything {@link SurvivalDemo} does with {@code AK47} the
 * constant works identically with an {@code ItemDefinition} that came out of a
 * text file, because both are the same kind of object.
 */
public final class JsonCatalogDemo {

    public static void main(String[] args) {
        List<ItemDefinition> loaded = CatalogLoader.loadResource(
                SurvivalItems.class, "/catalog/survival-items.json");

        ItemRegistry registry = new ItemRegistry().registerAll(loaded);
        System.out.printf("Loaded %d items from JSON, 0 Java classes changed.%n%n", registry.size());

        ItemDefinition ak47 = registry.require("ak47");
        System.out.println("ak47      : " + ak47 + " " + ak47.tags());
        System.out.println("damage    : " + ak47.attribute(SurvivalAttributes.DAMAGE).orElseThrow());
        System.out.println("caliber   : " + ak47.attribute(SurvivalAttributes.CALIBER).orElseThrow());
        System.out.println("magazine  : " + ak47.attribute(SurvivalAttributes.MAGAZINE_SIZE).orElseThrow());

        System.out.println();
        System.out.println("Same helpers work on the loaded registry:");
        List<ItemDefinition> feeders = Ammunition.weaponsFor(registry, registry.require("ammo_762x39"));
        System.out.println("  weapons sharing 7.62x39: " + feeders.stream().map(ItemDefinition::name).toList());

        System.out.println();
        System.out.println("Rebalancing the AK-47 now means editing one line of JSON, not recompiling:");
        System.out.println("  \"damage\": 35   ->   \"damage\": 40");
    }

    private JsonCatalogDemo() {
    }
}
