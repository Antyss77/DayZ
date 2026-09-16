package fr.antyss77.lootkit.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The mutable-over-time data of a single item: rounds loaded, durability left,
 * a custom name, who crafted it.
 *
 * <p>Immutable like everything else — {@link #with(Attribute, Object)} returns a
 * new state carrying the same {@link InstanceId}, so "the same rifle, one round
 * fewer" stays the same rifle.
 *
 * <p>A stack with a state holds exactly one unit: two rifles with different
 * ammo counts cannot share a slot, which is precisely why they need states.
 */
public final class ItemState {

    private final InstanceId id;
    private final Map<Attribute<?>, Object> values;

    private ItemState(InstanceId id, Map<Attribute<?>, Object> values) {
        this.id = id;
        this.values = Collections.unmodifiableMap(values);
    }

    /** A fresh state with a newly generated instance id. */
    public static ItemState create() {
        return new ItemState(InstanceId.random(), new LinkedHashMap<>());
    }

    /** Rebuilds a state from a save file, keeping its original id. */
    public static ItemState restore(InstanceId id) {
        return new ItemState(Objects.requireNonNull(id, "id"), new LinkedHashMap<>());
    }

    public InstanceId id() {
        return id;
    }

    public <T> Optional<T> get(Attribute<T> attribute) {
        Object value = values.get(attribute);
        return value == null ? Optional.empty() : Optional.of(attribute.cast(value));
    }

    public <T> T getOr(Attribute<T> attribute, T fallback) {
        return get(attribute).orElse(fallback);
    }

    public <T> ItemState with(Attribute<T> attribute, T value) {
        Map<Attribute<?>, Object> copy = new LinkedHashMap<>(values);
        copy.put(Objects.requireNonNull(attribute, "attribute"), Objects.requireNonNull(value, "value"));
        return new ItemState(id, copy);
    }

    public ItemState without(Attribute<?> attribute) {
        Map<Attribute<?>, Object> copy = new LinkedHashMap<>(values);
        copy.remove(attribute);
        return new ItemState(id, copy);
    }

    public Map<Attribute<?>, Object> values() {
        return values;
    }

    /** Identity is the instance id alone: same object, whatever happened to it. */
    @Override
    public boolean equals(Object o) {
        return o instanceof ItemState other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "#" + id.shortForm() + values;
    }
}
