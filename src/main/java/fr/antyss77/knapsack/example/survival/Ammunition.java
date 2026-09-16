package fr.antyss77.knapsack.example.survival;

import fr.antyss77.knapsack.core.Inventory;
import fr.antyss77.knapsack.core.ItemDefinition;
import fr.antyss77.knapsack.core.ItemRegistry;
import fr.antyss77.knapsack.core.ItemStack;
import fr.antyss77.knapsack.core.ItemState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.CALIBER;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.LOADED_ROUNDS;
import static fr.antyss77.knapsack.example.survival.SurvivalAttributes.MAGAZINE_SIZE;

/**
 * Links weapons to the ammunition they accept.
 *
 * <p>The link is <em>not</em> a list of weapon ids on each cartridge, nor a list
 * of cartridge ids on each weapon — both rot the moment you add an item. It is a
 * shared calibre: a weapon and a cartridge are compatible when they declare the
 * same {@link SurvivalAttributes#CALIBER}. Adding a new 7.62x39 rifle, or a new
 * 7.62x39 cartridge, requires editing nothing else.
 *
 * <p>How many rounds are loaded is per-instance state, so this class reads and
 * writes {@link ItemState} rather than the definition.
 */
public final class Ammunition {

    public static Optional<String> caliberOf(ItemDefinition item) {
        return item.attribute(CALIBER);
    }

    /** True when {@code ammo} can be loaded into {@code weapon}. */
    public static boolean accepts(ItemDefinition weapon, ItemDefinition ammo) {
        if (!ammo.hasTag("ammo") || !weapon.hasTag("firearm")) {
            return false;
        }
        Optional<String> weaponCaliber = caliberOf(weapon);
        return weaponCaliber.isPresent() && weaponCaliber.equals(caliberOf(ammo));
    }

    /** Every cartridge in the catalogue this weapon can fire. */
    public static List<ItemDefinition> compatibleAmmo(ItemRegistry registry, ItemDefinition weapon) {
        List<ItemDefinition> result = new ArrayList<>();
        for (ItemDefinition candidate : registry.withTag("ammo")) {
            if (accepts(weapon, candidate)) {
                result.add(candidate);
            }
        }
        return List.copyOf(result);
    }

    /** Every weapon in the catalogue that fires this cartridge. */
    public static List<ItemDefinition> weaponsFor(ItemRegistry registry, ItemDefinition ammo) {
        List<ItemDefinition> result = new ArrayList<>();
        for (ItemDefinition candidate : registry.withTag("firearm")) {
            if (accepts(candidate, ammo)) {
                result.add(candidate);
            }
        }
        return List.copyOf(result);
    }

    /** A brand new weapon: its own instance id, empty magazine. */
    public static ItemStack newWeapon(ItemDefinition weapon) {
        return ItemStack.of(weapon, ItemState.create().with(LOADED_ROUNDS, 0));
    }

    public static int loadedRounds(ItemStack weapon) {
        return weapon.stateOptional().map(state -> state.getOr(LOADED_ROUNDS, 0)).orElse(0);
    }

    public static int magazineSize(ItemDefinition weapon) {
        return weapon.attributeOr(MAGAZINE_SIZE, 0);
    }

    /**
     * Reloads the weapon sitting in {@code weaponSlot} from the same inventory,
     * consuming the rounds it takes.
     *
     * @return what was loaded, or {@link Reload#NOTHING} if the weapon was full,
     *         the slot held something else, or no compatible ammo was carried
     */
    public static Reload reload(Inventory inventory, int weaponSlot) {
        ItemStack stack = inventory.slot(weaponSlot).orElse(null);
        if (stack == null || !stack.item().hasTag("firearm")) {
            return Reload.NOTHING;
        }
        int capacity = magazineSize(stack.item());
        int missing = capacity - loadedRounds(stack);
        if (missing <= 0) {
            return Reload.NOTHING;
        }
        for (ItemStack candidate : inventory.contents()) {
            if (!accepts(stack.item(), candidate.item())) {
                continue;
            }
            int taken = inventory.remove(candidate.item(), Math.min(missing, inventory.count(candidate.item())));
            if (taken == 0) {
                continue;
            }
            ItemState state = stack.stateOptional().orElseGet(ItemState::create);
            ItemStack reloaded = stack.withState(state.with(LOADED_ROUNDS, loadedRounds(stack) + taken));
            inventory.set(weaponSlot, reloaded);
            return new Reload(taken, candidate.item());
        }
        return Reload.NOTHING;
    }

    /**
     * Fires one round.
     *
     * @return true if a round was available and spent
     */
    public static boolean fire(Inventory inventory, int weaponSlot) {
        ItemStack stack = inventory.slot(weaponSlot).orElse(null);
        if (stack == null || loadedRounds(stack) <= 0) {
            return false;
        }
        ItemState state = stack.stateOptional().orElseGet(ItemState::create);
        inventory.set(weaponSlot, stack.withState(state.with(LOADED_ROUNDS, loadedRounds(stack) - 1)));
        return true;
    }

    /** Outcome of a reload: how many rounds went in, and which cartridge. */
    public record Reload(int rounds, ItemDefinition ammo) {

        public static final Reload NOTHING = new Reload(0, null);

        public boolean happened() {
            return rounds > 0;
        }
    }

    private Ammunition() {
    }
}
