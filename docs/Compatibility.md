# Compatibility & Integrations

Divine Enchantments has **no required dependencies**. Everything below is optional and activates automatically when the matching mod is installed.

## At a glance

| Mod | What it enables |
| :-- | :-- |
| **Spartan Weaponry** | Melee weapon classification: sabers, rapiers, spears, pikes, lances, glaives, halberds, hammers, warhammers, maces, clubs, daggers, and more. |
| **Spartan Shields** | Shield enchantments such as Aperture, Braced Pivot, and Challenger's Mark. |
| **Better Survival** | Nunchaku support for Rune: Carnage, Education incompatibility, and fixed Range arrow damage. |
| **Rin's SoManyEnchantments** | Adept synergy for Riches, Multishot/Splitshot synergy, and several mutual exclusions. |
| **Lycanites Mobs** | Ally summoning and multiple creature-specific interactions. |
| **Potion Core** | Potion effect integrations for several enchantments, plus a dynamic negative-effect pool for Gambler's Gambit. |
| **Tough As Nails / Simple Difficulty** | Temperature, thirst, and infection-related utility. |
| **ItemPhysic Full** | Walk-over item pickup restoration. |
| **CollisionDamage** | Cushioned Shirt reduces kinetic collision damage from this mod. |
| **Dynamic Surroundings** | Dampening Strike and Quiet Chipping reduce sound-based mob detection. |
| **Infernal Mobs** | Suregrip blocks Sticky/Thieving disarms. |
| **JEI** | Enchanted-book information pages. |
| **Enchantment Descriptions** | Sneak tooltips for every enchantment. |

> **1.12.2 note:** Maces, sabers, rapiers, spears, pikes, lances, glaives, halberds, nunchaku, and crossbows are not vanilla 1.12.2 items. They are provided by 1.12.2 weapon mods such as Spartan Weaponry, Better Survival, and Crossbows Backport.

## Detailed notes

- **JEI (Just Enough Items):** registers description pages for all 73 enchanted books via `@JEIPlugin`.
- **Enchantment Descriptions:** full tooltip integration via `enchantment.<modid>.<name>.desc`. Colored names (`Rune: Carnage` green, `Voodoo` red, `Ancient: Kratos Blades` yellow) are preserved.
- **Potion Core:** `potioncore:extension` (Perpetual Crucible), `potioncore:solid_core` and `potioncore:weight` (Grounded Vanguard), `potioncore:vulnerable` (Breach), `potioncore:climb` (Arachnid Ascent), `potioncore:archery` (Archery), and the dynamic negative-effect pool (Gambler's Gambit).
- **Better Survival:** nunchaku classification (Rune: Carnage), Education mutual exclusion (Riches), and the Range damage fix.
- **SoManyEnchantments:** Adept scales Riches chance and XP; Rune: Arrow Piercing is incompatible with Pierceful; Strafe is incompatible with Feathered Draw.
- **CollisionDamage:** Cushioned Shirt explicitly handles its `FLY_INTO_WALL` and collision damage sources.
- **Dynamic Surroundings and sound-based targeting:** Dampening Strike and Quiet Chipping shrink the radius at which mobs can acquire the player as a target.
- **Infernal Mobs:** Suregrip cancels and restores items dropped by Sticky/Thieving affixes.
- **Spartan Weaponry & Spartan Shields:** weapon, shield, and crossbow classification support across all custom items.
- **Lycanites Mobs:** ephemeral Aegis summoning (`Ally`), projectile reflection (`Aperture`), anti-lift grounding (`Grounded Vanguard`), instability immunity (`Gyroscopic Plating`), Aphagia silencing (`Muzzle`), Decay infliction (`Necrotic Stasis`), Demon/Shadow detection (`Demonic Edge`), ore-spawn suppression (`Sober Chisel`), and boss classification.
- **Rin's SoManyEnchantments:** Multishot / Splitshot trailing arrows, Advanced Mending mutual exclusion (`Headhunter`), Advanced Looting drop amplification (`Riches`), Strengthened Vitality mutual exclusion (`Gourmand's Vitality`), and Arrow Recovery mutual exclusion (`Scavenger's Habit`).
- **Tough As Nails & Simple Difficulty:** body-temperature stabilization (`Thermal Foil`), parasite infection reduction from untreated water (`Desalination Mesh`), and hunger/thirst pause during stationary rest (`Metabolic Slumber`).
- **ItemPhysic Full:** automatic walk-over collision pickup restoration (`Ground Sweep`).

## Crossbow support

Crossbow enchantments (Muzzle, Ballistic Overdrive, Splintering Impact, and crossbow-compatible bow enchantments) require a **1.12.2 crossbow mod**. Spartan Weaponry and Crossbows Backport are both supported.

## Multiplayer

The mod is designed to be installed on both the client and server. Server-side logic is authoritative for combat, durability, AI, and inventory behavior; client-side code handles rendering, HUD icons, and visual synchronization.
