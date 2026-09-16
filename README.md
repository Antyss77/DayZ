# Knapsack

[![Build](https://github.com/Antyss77/Knapsack/actions/workflows/build.yml/badge.svg)](https://github.com/Antyss77/Knapsack/actions/workflows/build.yml)
[![](https://jitpack.io/v/Antyss77/Knapsack.svg)](https://jitpack.io/#Antyss77/Knapsack)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A small, dependency-free inventory system for Java 17+.

Slots, stacking, weight limits and per-container rules — the plumbing that every
game, RPG prototype, crafting sim or loot system ends up rewriting. Knapsack gives
you that layer as data-driven, immutable, unit-tested classes, and stays out of
the way of your own domain model.

```java
Inventory backpack = SlotInventory.withSlots(12)
        .rule(new WeightLimitRule(25.0))
        .build();

int leftover = backpack.add(bandage, 10);   // 0 = everything fit
backpack.remove(bandage, 3);                // returns how many were actually removed
```

## Why another inventory library

Most hobby inventory code models items with inheritance: one class per weapon,
one class per armour piece. It compiles, and then adding an item means writing a
class, rebalancing means a recompile, and loading a save file means a giant
`switch`.

Knapsack treats items as **data**:

| | Class-per-item | Knapsack |
|---|---|---|
| Add an item | new `.java` file | a few lines of data (or a JSON row) |
| Item properties | fields on subclasses | typed `Attribute<T>` keys |
| Categories | rigid class hierarchy | free-form tags, an item can have several |
| Load a save | map ids to classes by hand | `registry.require(id)` |

## Install

Via [JitPack](https://jitpack.io/#Antyss77/Knapsack), no cloning needed. Add the
JitPack repository, then the dependency:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Antyss77</groupId>
    <artifactId>Knapsack</artifactId>
    <version>v0.1.0</version>
</dependency>
```

(Gradle: `implementation 'com.github.Antyss77:Knapsack:v0.1.0'` with
`maven { url 'https://jitpack.io' }` in your repositories.) Any tag or commit
hash works as the version — check the badge above for what is currently built.

Not on Maven Central yet — see the roadmap. To hack on Knapsack itself, or to
vendor it without JitPack, clone it directly; it has no dependencies:

```bash
git clone https://github.com/Antyss77/Knapsack.git
cd Knapsack
mvn test
mvn compile exec:java      # runs the survival demo
```

## Core concepts

**`ItemDefinition`** — the immutable description of a *kind* of item, built once
and shared. Carries an id, a display name, a weight, a max stack size, tags and
typed attributes.

```java
Attribute<Integer> DAMAGE = Attribute.of("damage", Integer.class);

ItemDefinition ak47 = ItemDefinition.builder("ak47", "AK-47")
        .weight(3.6)
        .tag("weapon", "firearm")
        .attribute(DAMAGE, 35)
        .build();
```

**`ItemStack`** — an immutable *(item, amount)* pair, as held in one slot. The
constructor refuses amounts below 1 or above the item's max stack size, so an
invalid stack cannot exist.

**`Inventory` / `SlotInventory`** — a fixed number of slots with automatic
stacking. `add` returns the number of units that did **not** fit rather than a
boolean, so partial transfers never silently destroy items:

```java
int moved   = chest.remove(ammo, 60);
int bounced = pouch.add(ammo, moved);
chest.add(ammo, bounced);      // nothing is lost
```

**`InventoryRule`** — a pluggable constraint. Rules compose; the inventory keeps
the smallest amount every rule allows. Three ship with the library
(`WeightLimitRule`, `TagFilterRule`, `MaxQuantityRule`) and it is a functional
interface, so your own is a lambda:

```java
Inventory questBag = SlotInventory.withSlots(4)
        .rule((inv, item, amount) -> item.hasTag("quest") ? amount : 0)
        .build();
```

**`ItemState` / `InstanceId`** — per-instance data for items that cannot be
interchangeable: this rifle, with its own ammo count and its own generated id.
A stack carrying a state holds exactly one unit and never merges with another.

**`ItemRegistry`** — id → definition lookup. Save files store ids; the registry
rebuilds the inventory on load, which is what lets you rebalance an item without
migrating old saves.

**`acceptableAmount(item, n)`** answers "how many would fit right now?" without
mutating anything — handy for greying out a UI button or validating a trade
before committing to it.

## Two kinds of id

Most inventory code gets this wrong by picking one. You need both, and they are
generated in opposite ways:

| | `ItemDefinition.id()` | `InstanceId` |
|---|---|---|
| Names | a kind of item | one individual object |
| Example | `ak47`, `mymod:ak47` | `952a1e6d-9f4d-4cf8-...` |
| Written by | you, by hand | the library, `UUID.randomUUID()` |
| Used for | `/give ak47`, loot tables, save files, recipes | tracking, trades, logs, "who destroyed this?" |
| Stable | forever — renaming it breaks old saves | for the life of the object |

Definition ids must never be random: `/give 7f3a-91c2` is unusable, and a
regenerated id breaks every save file that referenced it. They are validated at
build time (lowercase, no spaces, optional `namespace:` prefix) precisely because
players type them:

```java
registry.resolve("AK47");        // case-insensitive, namespace optional
registry.suggest("ak", 5);       // ["ak47", "ak74"] — command completion, "did you mean?"
```

Instance ids are the opposite: generated, never authored. UUIDs rather than a
counter, so two servers or two save files can be merged without collisions. Ask
for one only when the item carries its own state — 60 identical rounds of ammo
do not need 60 ids.

```java
ItemStack rifle = ItemStack.unique(ak47);
inventory.add(rifle);
inventory.slotOf(rifle.instanceId().orElseThrow());   // find it again
inventory.removeInstance(id);                          // confiscate that exact one
```

## Ammunition, and other relations between items

The tempting model is a list of compatible weapon ids on each cartridge — which
means every new weapon requires editing every cartridge. Model the *shared
property* instead. Here, a calibre:

```java
ItemDefinition ak47 = ItemDefinition.builder("ak47", "AK-47")
        .tag("firearm").attribute(CALIBER, "762x39").attribute(MAGAZINE_SIZE, 30).build();

ItemDefinition ammo = ItemDefinition.builder("ammo_762x39", "7.62x39mm")
        .tag("ammo").attribute(CALIBER, "762x39").maxStackSize(60).build();
```

A new 7.62x39 rifle now works with every existing cartridge, and vice versa,
with nothing else to edit. `Ammunition` (in the survival example) does the
matching and the bookkeeping:

```java
Ammunition.compatibleAmmo(registry, ak47);   // what this weapon can fire
Ammunition.weaponsFor(registry, ammo);       // what this cartridge feeds
Ammunition.reload(backpack, weaponSlot);     // takes rounds from the bag, writes the count back
Ammunition.fire(backpack, weaponSlot);
```

Rounds loaded live in the weapon's `ItemState`, so reloading returns the same
instance id — it is still the same rifle.

## Effects

Effects are attached in a side table, per item *or* per tag, so a category can
grant behaviour to every item in it, including items added later:

```java
EffectTable effects = new EffectTable()
        .forTag("firearm", new Recoil(1.0))       // every firearm, present and future
        .forTag("incendiary", new Burn(6.0, 4.0))
        .forItem(pkm, new Recoil(2.5))            // this one kicks harder
        .forItem(molotov, new Burn(8.0, 7.0));    // this one burns longer

effects.of(molotov);                        // [KnockBack, Burn(8.0, 7.0)]
effects.of(ak47, Recoil.class);             // Optional[Recoil[1.0]]
```

Entries sharing an `Effect.id()` collapse, most specific wins, so an item
overrides its category instead of stacking a second copy. What an effect *does*
is your game loop's business — `Effect` is an empty interface on purpose, and
keeping the table separate from the catalogue lets one set of items drive
several rule sets (hardcore mode, PvE mode, a test fixture with effects off).

## Loading a catalogue from JSON

Everything above also works for items that were never written in Java. `CatalogLoader`
turns a JSON array into `ItemDefinition`s, with attribute types inferred from the
JSON value — a plain number becomes an `Integer` attribute, `2.5` becomes a
`Double`, text becomes a `String`:

```json
[
  {
    "id": "ak47",
    "name": "AK-47",
    "weight": 3.6,
    "tags": ["weapon", "firearm", "assault_rifle"],
    "attributes": { "damage": 35, "caliber": "762x39", "magazineSize": 30 }
  }
]
```

```java
List<ItemDefinition> items = CatalogLoader.loadResource(MyGame.class, "/catalog/items.json");
ItemRegistry registry = new ItemRegistry().registerAll(items);

registry.require("ak47").attribute(SurvivalAttributes.DAMAGE);   // Optional[35]
```

That last line is the point: as long as your hand-declared `Attribute.of("damage",
Integer.class)` constant agrees with the JSON on key and type, an item loaded from
a text file is indistinguishable from one built with the typed builder. Rebalancing
becomes editing a number in JSON, not recompiling.

`CatalogLoader.load(Path)` reads a file from disk; `CatalogLoader.loadResource(...)`
reads one bundled inside your jar. Parsing itself has no third-party dependency —
see [`example/survival/survival-items.json`](src/main/resources/catalog/survival-items.json)
for the full sample catalogue, and `JsonCatalogDemo` for it loaded and used side by
side with the hand-written one.

## Example

[`example/survival`](src/main/java/fr/antyss77/knapsack/example/survival) is a
complete sample: a catalogue of weapons, ammo, armour and consumables, a
weight-limited backpack, and an ammo pouch that refuses anything untagged.
Run it with `mvn compile exec:java`, then delete the package — nothing in the
core depends on it.

## Roadmap

- [x] Per-instance state: ammo loaded, durability, unique ids
- [x] Ammunition compatibility and reloading
- [x] Item and category effects
- [x] JSON loading for catalogues
- [ ] Grid inventories (multi-cell items, Tetris-style)
- [ ] Equipment slots (head / chest / hands) as a separate container type
- [ ] Transfer helper: move a stack between two inventories atomically
- [ ] Inventory events / listeners
- [ ] Publish to Maven Central

Issues and pull requests are welcome, especially on the items above — see
[CONTRIBUTING.md](CONTRIBUTING.md) before opening one.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

MIT — see [LICENSE](LICENSE).
