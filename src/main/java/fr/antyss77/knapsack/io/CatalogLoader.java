package fr.antyss77.knapsack.io;

import fr.antyss77.knapsack.core.ItemDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builds {@link ItemDefinition}s from a JSON catalogue instead of Java code —
 * the difference between a project that recompiles to rebalance an item and
 * one that edits a text file.
 *
 * <p>Expected shape: a JSON array of objects, {@code id} and {@code name}
 * required, everything else optional:
 *
 * <pre>{@code
 * [
 *   {
 *     "id": "ak47",
 *     "name": "AK-47",
 *     "weight": 3.6,
 *     "maxStackSize": 1,
 *     "tags": ["weapon", "firearm", "assault_rifle"],
 *     "attributes": {
 *       "damage": 35,
 *       "caliber": "762x39",
 *       "magazineSize": 30
 *     }
 *   }
 * ]
 * }</pre>
 *
 * <p>Each entry in {@code attributes} becomes an {@link ItemDefinition}
 * attribute via {@link ItemDefinition.Builder#attribute(String, Object)}: the
 * key is the attribute's name and its type is inferred from the JSON value
 * (a whole number becomes an {@code Integer} attribute, {@code 2.5} becomes a
 * {@code Double}, text becomes a {@code String}). Declare your
 * {@code Attribute<T>} constants — as {@code SurvivalAttributes} does in the
 * example — with the matching key and boxed type, and definitions loaded from
 * JSON are indistinguishable from ones built by hand.
 *
 * <p>Parsing uses a small internal JSON reader with no third-party dependency.
 * If your project already has a JSON library you prefer, use it to produce
 * items directly through {@link ItemDefinition#builder(String, String)}
 * instead — this class is a convenience, not the only way in.
 */
public final class CatalogLoader {

    /** Parses a catalogue from a JSON string already in memory. */
    public static List<ItemDefinition> parse(String json) {
        Objects.requireNonNull(json, "json");
        Object root = MiniJson.parse(json);
        if (!(root instanceof List<?> entries)) {
            throw new IllegalArgumentException("catalogue root must be a JSON array of items");
        }
        List<ItemDefinition> items = new ArrayList<>(entries.size());
        for (Object entry : entries) {
            if (!(entry instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException("each catalogue entry must be a JSON object, got " + entry);
            }
            items.add(toItemDefinition(map));
        }
        return List.copyOf(items);
    }

    /** Reads and parses a catalogue from a file on disk. */
    public static List<ItemDefinition> load(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return parse(Files.readString(path, StandardCharsets.UTF_8));
    }

    /**
     * Reads and parses a catalogue bundled as a classpath resource, e.g.
     * {@code CatalogLoader.loadResource(SurvivalItems.class, "/catalog/survival-items.json")}.
     * Throws {@link UncheckedIOException} rather than a checked one: a missing
     * bundled resource is a packaging bug, not a recoverable condition.
     */
    public static List<ItemDefinition> loadResource(Class<?> anchor, String resourcePath) {
        Objects.requireNonNull(anchor, "anchor");
        Objects.requireNonNull(resourcePath, "resourcePath");
        try (InputStream in = anchor.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("resource not found on classpath: " + resourcePath);
            }
            return parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("failed to read resource " + resourcePath, e);
        }
    }

    private static ItemDefinition toItemDefinition(Map<?, ?> map) {
        String id = requireString(map, "id");
        String name = requireString(map, "name");
        ItemDefinition.Builder builder = ItemDefinition.builder(id, name);

        Object weight = map.get("weight");
        if (weight != null) {
            builder.weight(toDouble(weight, "weight"));
        }
        Object maxStackSize = map.get("maxStackSize");
        if (maxStackSize != null) {
            builder.maxStackSize(toInt(maxStackSize, "maxStackSize"));
        }
        Object tags = map.get("tags");
        if (tags instanceof List<?> list) {
            for (Object tag : list) {
                builder.tag(String.valueOf(tag));
            }
        } else if (tags != null) {
            throw new IllegalArgumentException("'tags' must be a JSON array in item '" + id + "'");
        }
        Object attributes = map.get("attributes");
        if (attributes instanceof Map<?, ?> attributeMap) {
            for (Map.Entry<?, ?> entry : attributeMap.entrySet()) {
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                builder.attribute(String.valueOf(entry.getKey()), value);
            }
        } else if (attributes != null) {
            throw new IllegalArgumentException("'attributes' must be a JSON object in item '" + id + "'");
        }
        return builder.build();
    }

    private static String requireString(Map<?, ?> map, String field) {
        Object value = map.get(field);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("item is missing required field '" + field + "'");
        }
        return text;
    }

    private static double toDouble(Object value, String field) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        throw new IllegalArgumentException("'" + field + "' must be a number, got " + value);
    }

    private static int toInt(Object value, String field) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new IllegalArgumentException("'" + field + "' must be a number, got " + value);
    }

    private CatalogLoader() {
    }
}
