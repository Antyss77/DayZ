package fr.antyss77.knapsack.core;

import java.util.Objects;

/**
 * A typed key used to attach arbitrary data to an {@link ItemDefinition}.
 *
 * <p>Attributes are how this library stays domain-agnostic: the core knows
 * nothing about damage, durability or armour. Your game (or app) declares the
 * keys it needs and the compiler still checks the value types.
 *
 * <pre>{@code
 * public static final Attribute<Integer> DAMAGE = Attribute.of("damage", Integer.class);
 *
 * ItemDefinition ak = ItemDefinition.builder("ak47", "AK-47")
 *         .attribute(DAMAGE, 35)
 *         .build();
 *
 * int damage = ak.attributeOr(DAMAGE, 0);
 * }</pre>
 *
 * @param <T> type of the value stored under this key
 */
public final class Attribute<T> {

    private final String key;
    private final Class<T> type;

    private Attribute(String key, Class<T> type) {
        this.key = Objects.requireNonNull(key, "key");
        this.type = Objects.requireNonNull(type, "type");
    }

    public static <T> Attribute<T> of(String key, Class<T> type) {
        return new Attribute<>(key, type);
    }

    public String key() {
        return key;
    }

    public Class<T> type() {
        return type;
    }

    /** Checks and casts a raw value to this attribute's type. */
    T cast(Object value) {
        return type.cast(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute<?> other)) {
            return false;
        }
        return key.equals(other.key) && type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, type);
    }

    @Override
    public String toString() {
        return key;
    }
}
