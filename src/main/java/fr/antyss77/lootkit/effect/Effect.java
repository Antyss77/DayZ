package fr.antyss77.lootkit.effect;

/**
 * Something an item does beyond sitting in a bag: burn on impact, blind, slow
 * the carrier, repair nearby gear.
 *
 * <p>The library deliberately does not define what an effect <em>means</em> —
 * that belongs to your game loop. It defines how effects are attached to items
 * and categories, and how they are looked up. Implement it as a record, one per
 * kind of effect, with its own tuning values:
 *
 * <pre>{@code
 * public record Burn(double damagePerSecond, double seconds) implements Effect {
 *     public String id() { return "burn"; }
 * }
 * }</pre>
 */
public interface Effect {

    /**
     * Stable identifier of the <em>kind</em> of effect. Two effects sharing an
     * id are the same effect with different tuning: the more specific one wins
     * in an {@link EffectTable}.
     */
    String id();
}
