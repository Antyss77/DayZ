package fr.antyss77.knapsack.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Lookup table of every known {@link ItemDefinition}, keyed by id.
 *
 * <p>Saved games store ids, not objects: on load you read an id, ask the
 * registry for the definition, and rebuild the inventory. That indirection is
 * what lets you rebalance an item without migrating old save files.
 */
public final class ItemRegistry {

    private final Map<String, ItemDefinition> byId = new LinkedHashMap<>();

    public ItemRegistry register(ItemDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        ItemDefinition previous = byId.putIfAbsent(definition.id(), definition);
        if (previous != null) {
            throw new IllegalStateException("duplicate item id: " + definition.id());
        }
        return this;
    }

    public ItemRegistry registerAll(Collection<ItemDefinition> definitions) {
        definitions.forEach(this::register);
        return this;
    }

    public Optional<ItemDefinition> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** Like {@link #find(String)} but fails loudly — use when the id must exist. */
    public ItemDefinition require(String id) {
        ItemDefinition definition = byId.get(id);
        if (definition == null) {
            throw new NoSuchElementException("unknown item id: " + id);
        }
        return definition;
    }

    public List<ItemDefinition> withTag(String tag) {
        List<ItemDefinition> result = new ArrayList<>();
        for (ItemDefinition definition : byId.values()) {
            if (definition.hasTag(tag)) {
                result.add(definition);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public Collection<ItemDefinition> all() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public int size() {
        return byId.size();
    }

    /**
     * Command-friendly lookup: case-insensitive, and tolerant of a missing
     * namespace ({@code ak47} finds {@code mymod:ak47} when only one namespace
     * declares it).
     */
    public Optional<ItemDefinition> resolve(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        String needle = input.trim().toLowerCase(Locale.ROOT);
        ItemDefinition exact = byId.get(needle);
        if (exact != null) {
            return Optional.of(exact);
        }
        if (needle.indexOf(':') < 0) {
            List<ItemDefinition> matches = new ArrayList<>();
            for (Map.Entry<String, ItemDefinition> entry : byId.entrySet()) {
                int colon = entry.getKey().indexOf(':');
                if (colon >= 0 && entry.getKey().substring(colon + 1).equals(needle)) {
                    matches.add(entry.getValue());
                }
            }
            // Ambiguous input is a failure, not a coin flip.
            if (matches.size() == 1) {
                return Optional.of(matches.get(0));
            }
        }
        return Optional.empty();
    }

    /**
     * Ids matching a partial input, for command completion and for "did you
     * mean?" error messages. Prefix matches come first.
     */
    public List<String> suggest(String partial, int limit) {
        String needle = partial == null ? "" : partial.trim().toLowerCase(Locale.ROOT);
        List<String> prefixed = new ArrayList<>();
        List<String> contained = new ArrayList<>();
        for (String id : byId.keySet()) {
            if (id.startsWith(needle)) {
                prefixed.add(id);
            } else if (id.contains(needle)) {
                contained.add(id);
            }
        }
        prefixed.addAll(contained);
        return List.copyOf(prefixed.subList(0, Math.min(limit, prefixed.size())));
    }
}
