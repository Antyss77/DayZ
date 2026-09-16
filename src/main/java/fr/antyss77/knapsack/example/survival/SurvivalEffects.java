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

    /** Area damage on detonation, on top of whatever direct damage applies. */
    public record Explosion(double damage, double radius) implements Effect {
        @Override
        public String id() {
            return "explosion";
        }
    }

    /**
     * Category rules first, then per-item exceptions.
     *
     * <p>Every firearm recoils, every blade makes its target bleed, every
     * explosive damages an area — none of that names a single weapon, so a
     * rifle or a grenade added tomorrow inherits it for free. The heavier
     * weapons and the molotov then override what their category grants them,
     * because a PKM kicks harder than a pistol and a molotov burns longer than
     * a plain incendiary.
     */
    public static EffectTable table() {
        return new EffectTable()
                .forTag("firearm", new Recoil(1.0))
                .forTag("knife", new Bleed(1.5))
                .forTag("sword", new Bleed(3.0))
                .forTag("axe", new Bleed(2.5))
                .forTag("throwable", new KnockBack(1.5))
                .forTag("incendiary", new Burn(6.0, 4.0))
                .forTag("explosive", new Explosion(60.0, 4.0))
                .forTag("flashbang", new Blind(5.0))
                .forItem(SurvivalItems.PKM, new Recoil(2.5))
                .forItem(SurvivalItems.KAR98K, new Recoil(3.0))
                .forItem(SurvivalItems.REMINGTON_870, new Recoil(2.0))
                .forItem(SurvivalItems.MOLOTOV, new Burn(8.0, 7.0))
                .forItem(SurvivalItems.FRAG_GRENADE, new Explosion(75.0, 5.0))
                .forItem(SurvivalItems.SMOKE_GRENADE, new Blind(3.0));
    }

    private SurvivalEffects() {
    }
}
