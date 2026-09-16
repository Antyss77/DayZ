# Contributing to Knapsack

Thanks for considering a contribution. This project is small on purpose —
please read the two rules below before writing code, they will save you a
rewrite.

## The two rules

1. **The `core` package stays generic.** Nothing under
   `fr.antyss77.knapsack.core` may reference weapons, armour, survival games,
   or any other domain concept. If your change needs a game-specific idea
   (damage, calibre, a body slot), it belongs in `example/survival` or in your
   own project's code, expressed through `Attribute<T>`, tags, or an
   `InventoryRule` — not through a new field on `ItemDefinition`.
2. **Every change ships with a test.** `SlotInventory`, `ItemDefinition`,
   `CatalogLoader` and friends are meant to be trustworthy enough that a game
   built on top of them doesn't need its own inventory tests. A pull request
   that changes behaviour without a test that would have caught the bug will
   be asked to add one before it's merged.

If you're unsure whether an idea fits rule 1, open an issue first — it's a lot
easier to discuss than to unwind.

## Getting set up

```bash
git clone https://github.com/Antyss77/Knapsack.git
cd Knapsack
mvn test                    # compiles and runs the full test suite
mvn compile exec:java       # runs the survival demo (SurvivalDemo)
```

Requires a JDK 17 or newer. No other tooling is needed — the project has zero
runtime dependencies, and JUnit 5 is the only test dependency.

## Making a change

1. Fork the repository and create a branch off `main`.
2. Write the change. If you're adding a new package-level concept (a new rule,
   a new effect, a new example domain), skim an existing class of the same
   kind first — `WeightLimitRule`, `SurvivalEffects` — and match its style:
   Javadoc explaining the *why*, not just the *what*, and small, focused
   classes over large ones.
3. Add or update tests under `src/test/java`, mirroring the package of the
   class you changed.
4. Run `mvn test` and make sure everything passes, including the tests you
   didn't write.
5. Open a pull request. Describe what changed and why in a sentence or two —
   the diff already shows *what*, the description should carry the *why* that
   isn't obvious from the code.

## What kinds of contributions are especially welcome

- Items checked in the [README roadmap](README.md#roadmap) — grid inventories,
  equipment slots, inventory events, a transfer helper between two containers.
- More entries in the JSON example catalogue
  (`src/main/resources/catalog/survival-items.json`), or a second example
  domain entirely (a warehouse, a crafting game) that proves the core is not
  secretly tied to weapons and armour.
- Bug reports with a failing test attached — the fastest way to get something
  fixed is to show, in code, what should have happened.

## Reporting a bug without a fix

Open an issue with:
- What you expected to happen and what happened instead.
- A minimal snippet that reproduces it — a dozen lines building an
  `ItemDefinition` and an `Inventory` is usually enough.
- Your JDK version (`java -version`).

## Code style

- Follow the formatting already in the file you're editing; there is no
  separate style guide beyond "match your neighbours".
- Prefer immutable types and small, composable pieces (`InventoryRule` as a
  functional interface, `Effect` as a marker interface) over large
  configurable classes.
- Public API gets Javadoc. Explain the *reason* a type exists, not just what
  its methods do — see `InstanceId` or `EffectTable` for the tone to aim for.

## License

By contributing, you agree that your contribution is licensed under the
project's [MIT License](LICENSE).
