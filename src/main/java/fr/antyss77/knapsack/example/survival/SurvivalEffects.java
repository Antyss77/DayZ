package fr.antyss77.knapsack.example.survival;

import fr.antyss77.knapsack.effect.Effect;
import fr.antyss77.knapsack.effect.EffectTable;

/**
 * Sample effects and the table that wires them to items and categories.
 *
 * <p>Each effect is a record: its fields are its tuning values, so the same
 * effect can be attached twice with different numbers.
 */
public final class SurvivalEffects {

    /** Damage over time after impact. */
    public record Burn(double damagePerSecond, double seconds) implements Effect {
        @Override
        public String id() {
            return "burn";
        }
    }

    /** Blinds anyone looking at the detonation. */
    public record Blind(double seconds) implements Effect {
        @Override
        public String id() {
            return "blind";
        }
    }

    /** Pushes targets away from the point of impact. */
    public record KnockBack(double strength) implements Effect {
        @Override
        public String id() {
            return "knockback";
        }
    }

    /** Wound that keeps dealing damage until bandaged. */
    public record Bleed(double damagePerSecond) implements Effect {
        @Override
        public String id() {
            return "bleed";
        }
    }

    /** Aim penalty after each shot. */
    public record Recoil(double strength) implements Effect {
        @Override
        public String id() {
            return "recoil";
        }
    }

    /**
     * Category rules first, then per-item exceptions.
     *
     * <p>Every firearm recoils and every blade makes its target bleed without
     * naming a single weapon — a rifle added tomorrow inherits both. The molotov
     * then overrides the burn it gets as an incendiary, because it should burn
     * longer than the category default.
     */
    public static EffectTable table() {
        return new EffectTable()
                .forTag("firearm", new Recoil(1.0))
                .forTag("knife", new Bleed(1.5))
                .forTag("sword", new Bleed(3.0))
                .forTag("throwable", new KnockBack(1.5))
                .forTag("incendiary", new Burn(6.0, 4.0))
                .forItem(SurvivalItems.PKM, new Recoil(2.5))
                .forItem(SurvivalItems.MOLOTOV, new Burn(8.0, 7.0))
                .forItem(SurvivalItems.SMOKE_GRENADE, new Blind(3.0));
    }

    private SurvivalEffects() {
    }
}
