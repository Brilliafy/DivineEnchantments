# Development

## Requirements

- JDK 8
- The included Gradle wrapper
- Minecraft Forge 1.12.2 / ForgeGradle 2.3

## Build

```bash
./gradlew build
```

The reobfuscated mod jar is written to:

```text
build/libs/DivineEnchantments-1.12.2-1.0.7.jar
```

## Local server test

```bash
./gradlew runServer
```

The `run/` directory is a disposable development server and is ignored by Git.

## Project layout

```text
src/main/java/com/divineenchantments/
├── enchantment/   Enchantment classes, including EnchantingTableTiers.java
├── event/         Forge event handlers
├── entity/        Custom entities (Voodoo ally, Kratos chain)
├── network/       SimpleNetworkWrapper packets
├── potion/        Custom potion effect + registry
├── client/        Renderers and client-only helpers
├── bullseye/      Bullseye state and sync
├── enlargement/   Enlargement sync helper
├── init/          Enchantment registration
├── util/          Item classification helpers
└── compat/        Optional mod integrations

src/main/resources/
├── assets/divineenchantments/lang/        en_us.lang, en_US.lang
├── assets/divineenchantments/textures/    potion icon, bullseye marker
├── logo.png                                mod icon
└── mcmod.info                              mod metadata
```

## Adding an enchantment

1. Create a class extending `DivineEnchantment`.
2. Register it in `init/ModEnchantments.java`.
3. If it is table-available, add its registry path and first table level to `EnchantingTableTiers.java`.
4. Add the name and `.desc` keys to both language files.
5. Add the gameplay hook in the relevant event handler.
6. Update `docs/EnchantingTable.md` with the new table range and tier unlocks.
7. JEI and Enchantment Descriptions pick it up automatically.

## Notes

- `build/`, `run/`, `.gradle/`, and IDE files are ignored.
- Optional integrations use `compileOnly` dependencies and runtime `Loader.isModLoaded` checks.
