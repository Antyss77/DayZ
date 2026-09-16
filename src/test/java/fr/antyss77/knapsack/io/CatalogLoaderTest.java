package fr.antyss77.knapsack.io;

import fr.antyss77.knapsack.core.Attribute;
import fr.antyss77.knapsack.core.ItemDefinition;
import fr.antyss77.knapsack.core.ItemRegistry;
import fr.antyss77.knapsack.example.survival.SurvivalAttributes;
import fr.antyss77.knapsack.example.survival.SurvivalItems;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogLoaderTest {

    @Test
    void parsesRequiredAndOptionalFields() {
        String json = """
                [
                  {
                    "id": "bandage",
                    "name": "Bandage",
                    "weight": 0.05,
                    "maxStackSize": 10,
                    "tags": ["consumable", "medical"]
                  }
                ]
                """;

        List<ItemDefinition> items = CatalogLoader.parse(json);

        assertEquals(1, items.size());
        ItemDefinition bandage = items.get(0);
        assertEquals("bandage", bandage.id());
        assertEquals("Bandage", bandage.name());
        assertEquals(0.05, bandage.weight(), 1e-9);
        assertEquals(10, bandage.maxStackSize());
        assertTrue(bandage.hasTag("medical"));
    }

    @Test
    void defaultsMatchTheBuilderWhenFieldsAreOmitted() {
        String json = """
                [ { "id": "raw", "name": "Raw item" } ]
                """;

        ItemDefinition item = CatalogLoader.parse(json).get(0);

        assertEquals(0.0, item.weight(), 1e-9);
        assertEquals(1, item.maxStackSize());
        assertTrue(item.tags().isEmpty());
    }

    @Test
    void wholeNumberAttributesAreFoundByIntegerConstants() {
        String json = """
                [
                  {
                    "id": "ak47", "name": "AK-47",
                    "attributes": { "damage": 35, "caliber": "762x39", "magazineSize": 30 }
                  }
                ]
                """;

        ItemDefinition ak47 = CatalogLoader.parse(json).get(0);

        assertEquals(35, ak47.attribute(SurvivalAttributes.DAMAGE).orElseThrow());
        assertEquals("762x39", ak47.attribute(SurvivalAttributes.CALIBER).orElseThrow());
        assertEquals(30, ak47.attribute(SurvivalAttributes.MAGAZINE_SIZE).orElseThrow());
    }

    @Test
    void decimalAttributesAreFoundByDoubleConstants() {
        String json = """
                [ { "id": "molotov", "name": "Molotov", "attributes": { "fuseSeconds": 2.5 } } ]
                """;

        ItemDefinition molotov = CatalogLoader.parse(json).get(0);

        assertEquals(2.5, molotov.attribute(SurvivalAttributes.FUSE_SECONDS).orElseThrow(), 1e-9);
    }

    @Test
    void rejectsAnEntryMissingARequiredField() {
        assertThrows(IllegalArgumentException.class,
                () -> CatalogLoader.parse("[ { \"name\": \"No id\" } ]"));
    }

    @Test
    void rejectsATagsFieldThatIsNotAnArray() {
        assertThrows(IllegalArgumentException.class,
                () -> CatalogLoader.parse("[ { \"id\": \"x\", \"name\": \"X\", \"tags\": \"weapon\" } ]"));
    }

    @Test
    void rejectsMalformedJson() {
        assertThrows(IllegalArgumentException.class, () -> CatalogLoader.parse("[ { \"id\": }"));
    }

    @Test
    void bundledSurvivalCatalogueMatchesTheHandWrittenOne() {
        List<ItemDefinition> loaded = CatalogLoader.loadResource(
                SurvivalItems.class, "/catalog/survival-items.json");
        ItemRegistry fromJson = new ItemRegistry().registerAll(loaded);
        ItemRegistry fromJava = SurvivalItems.registry();

        assertEquals(fromJava.size(), fromJson.size());

        Set<String> javaIds = fromJava.all().stream()
                .map(ItemDefinition::id).collect(Collectors.toCollection(TreeSet::new));
        Set<String> jsonIds = fromJson.all().stream()
                .map(ItemDefinition::id).collect(Collectors.toCollection(TreeSet::new));
        assertEquals(javaIds, jsonIds);

        ItemDefinition ak47 = fromJson.require("ak47");
        assertEquals(35, ak47.attribute(SurvivalAttributes.DAMAGE).orElseThrow());
        assertTrue(ak47.hasTag("assault_rifle"));
    }

    @Test
    void loadingFromAMissingResourceFailsClearly() {
        assertThrows(IllegalArgumentException.class,
                () -> CatalogLoader.loadResource(SurvivalItems.class, "/catalog/does-not-exist.json"));
    }

    @Test
    void looseAttributeMatchesTheTypedConstantOnlyWhenTypesAgree() {
        Attribute<Integer> intDamage = Attribute.of("damage", Integer.class);
        Attribute<String> stringDamage = Attribute.of("damage", String.class);

        ItemDefinition item = ItemDefinition.builder("x", "X").attribute("damage", 10).build();

        assertTrue(item.attribute(intDamage).isPresent());
        assertTrue(item.attribute(stringDamage).isEmpty(), "different type, same key: no match");
    }
}
