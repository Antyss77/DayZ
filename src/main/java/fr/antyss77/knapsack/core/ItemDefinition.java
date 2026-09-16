package fr.antyss77.knapsack.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * The immutable description of a kind of item: "an AK-47", not "the AK-47 in
 * slot 3". Instances are shared, never copied per inventory slot — quantities
 * and ownership live in {@link ItemStack} and {@link Inventory}.
 *
 * <p>Definitions are data, not classes. You do not subclass this type to add a
 * new item; you build one, optionally with {@link Attribute}s and tags:
 *
 * <pre>{@code
 * ItemDefinition medkit = ItemDefinition.builder("medkit", "First aid kit")
 *         .weight(0.4)
 *         .maxStackSize(3)
 *         .tag("consumable")
 *         .build();
 * }</pre>
 */
public final class ItemDefinition {

    private final String id;
    private final String name;
    private final double weight;
    private final int maxStackSize;
    private final Set<String> tags;
    private final Map<Attribute<?>, Object> attributes;

    private ItemDefinition(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.weight = builder.weight;
        this.maxStackSize = builder.maxStackSize;
        this.tags = Collections.unmodifiableSet(new LinkedHashSet<>(builder.tags));
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(builder.attributes));
    }

    public static Builder builder(String id, String name) {
        return new Builder(id, name);
    }

    /** Stable, unique identifier — use this for persistence, never the name. */
    public String id() {
        return id;
    }

    /** Human readable label, safe to translate or change. */
    public String name() {
        return name;
    }

    /** Weight of a single unit, in whatever unit your project uses. */
    public double weight() {
        return weight;
    }

    /** How many units fit in one slot. 1 means the item does not stack. */
    public int maxStackSize() {
        return maxStackSize;
    }

    public Set<String> tags() {
        return tags;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public <T> Optional<T> attribute(Attribute<T> attribute) {
        Object value = attributes.get(attribute);
        return value == null ? Optional.empty() : Optional.of(attribute.cast(value));
    }

    public <T> T attributeOr(Attribute<T> attribute, T fallback) {
        return attribute(attribute).orElse(fallback);
    }

    public Map<Attribute<?>, Object> attributes() {
        return attributes;
    }

    /** Returns a builder pre-filled with this definition, for variants. */
    public Builder toBuilder() {
        Builder builder = new Builder(id, name)
                .weight(weight)
                .maxStackSize(maxStackSize);
        builder.tags.addAll(tags);
        builder.attributes.putAll(attributes);
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ItemDefinition other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

    public static final class Builder {

        private static final java.util.regex.Pattern ID_PATTERN =
                java.util.regex.Pattern.compile("([a-z0-9_-]+:)?[a-z0-9_-]+");

        private final String id;
        private final String name;
        private double weight;
        private int maxStackSize = 1;
        private final Set<String> tags = new LinkedHashSet<>();
        private final Map<Attribute<?>, Object> attributes = new LinkedHashMap<>();

        private Builder(String id, String name) {
            this.id = requireValidId(id);
            this.name = requireText(name, "name");
        }

        public Builder weight(double weight) {
            if (weight < 0) {
                throw new IllegalArgumentException("weight must be >= 0, got " + weight);
            }
            this.weight = weight;
            return this;
        }

        public Builder maxStackSize(int maxStackSize) {
            if (maxStackSize < 1) {
                throw new IllegalArgumentException("maxStackSize must be >= 1, got " + maxStackSize);
            }
            this.maxStackSize = maxStackSize;
            return this;
        }

        public Builder tag(String... values) {
            for (String value : values) {
                tags.add(requireText(value, "tag"));
            }
            return this;
        }

        public <T> Builder attribute(Attribute<T> attribute, T value) {
            attributes.put(
                    Objects.requireNonNull(attribute, "attribute"),
                    Objects.requireNonNull(value, "value"));
            return this;
        }

        public ItemDefinition build() {
            return new ItemDefinition(this);
        }

        /**
         * Ids are typed by players in commands and stored in save files, so they
         * are constrained on purpose: lowercase, no spaces, optionally namespaced
         * as {@code mymod:ak47}. Never generate them randomly — a random id is
         * unusable in {@code /give} and changes between runs.
         */
        private static String requireValidId(String id) {
            requireText(id, "id");
            if (!ID_PATTERN.matcher(id).matches()) {
                throw new IllegalArgumentException(
                        "invalid item id '" + id + "': expected [namespace:]name using a-z, 0-9, _, -");
            }
            return id;
        }

        private static String requireText(String value, String field) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(field + " must not be blank");
            }
            return value;
        }
    }
}
