package fr.antyss77.lootkit.effect;

import fr.antyss77.lootkit.core.ItemDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Attaches {@link Effect}s to items, either one by one or to a whole category
 * through tags — "every throwable knocks back", "every incendiary burns" — so a
 * new item inherits the behaviour of its category for free.
 *
 * <pre>{@code
 * EffectTable effects = new EffectTable()
 *         .forTag("throwable", new KnockBack(2.0))
 *         .forItem(molotov, new Burn(8.0, 5.0));
 *
 * List<Effect> onImpact = effects.of(molotov);   // KnockBack + Burn
 * }</pre>
 *
 * <p>Resolution order: tag effects first, in the order the tags were declared,
 * then item effects. When two entries share an {@link Effect#id()}, the more
 * specific one wins — an item can override what its category grants it, which
 * is how you make one grenade burn longer than every other grenade.
 *
 * <p>Keeping effects in a side table rather than inside {@link ItemDefinition}
 * means the same catalogue can drive different rule sets: a hardcore mode, a
 * PvE mode, a test fixture with every effect disabled.
 */
public final class EffectTable {

    private final Map<String, List<Effect>> byItemId = new LinkedHashMap<>();
    private final Map<String, List<Effect>> byTag = new LinkedHashMap<>();

    public EffectTable forItem(ItemDefinition item, Effect... effects) {
        Objects.requireNonNull(item, "item");
        byItemId.computeIfAbsent(item.id(), key -> new ArrayList<>()).addAll(List.of(effects));
        return this;
    }

    public EffectTable forTag(String tag, Effect... effects) {
        Objects.requireNonNull(tag, "tag");
        byTag.computeIfAbsent(tag, key -> new ArrayList<>()).addAll(List.of(effects));
        return this;
    }

    /** Every effect that applies to this item, most specific last. */
    public List<Effect> of(ItemDefinition item) {
        Objects.requireNonNull(item, "item");
        Map<String, Effect> resolved = new LinkedHashMap<>();
        for (Map.Entry<String, List<Effect>> entry : byTag.entrySet()) {
            if (item.hasTag(entry.getKey())) {
                putAll(resolved, entry.getValue());
            }
        }
        putAll(resolved, byItemId.getOrDefault(item.id(), List.of()));
        return List.copyOf(resolved.values());
    }

    /** The resolved effect of a given type, if the item has one. */
    public <T extends Effect> Optional<T> of(ItemDefinition item, Class<T> type) {
        for (Effect effect : of(item)) {
            if (type.isInstance(effect)) {
                return Optional.of(type.cast(effect));
            }
        }
        return Optional.empty();
    }

    public boolean has(ItemDefinition item, String effectId) {
        return of(item).stream().anyMatch(effect -> effect.id().equals(effectId));
    }

    /** Every effect id this table can produce, for docs or debug screens. */
    public Set<String> knownEffectIds() {
        Set<String> ids = new LinkedHashSet<>();
        collectIds(byTag.values(), ids);
        collectIds(byItemId.values(), ids);
        return Set.copyOf(ids);
    }

    private static void putAll(Map<String, Effect> target, List<Effect> effects) {
        for (Effect effect : effects) {
            // Re-inserting under an existing key keeps the original position,
            // so an override does not reshuffle the resolution order.
            target.put(effect.id(), effect);
        }
    }

    private static void collectIds(Collection<List<Effect>> source, Set<String> target) {
        for (List<Effect> effects : source) {
            for (Effect effect : effects) {
                target.add(effect.id());
            }
        }
    }
}
