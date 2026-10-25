# Divine Enchantments

**71 handcrafted enchantments for Minecraft 1.12.2 - built for Forge and Cleanroom.**

> **1.12.2 note:** Maces, sabers, rapiers, spears, pikes, lances, glaives, halberds, nunchaku, and crossbows come from 1.12.2 weapon mods such as Spartan Weaponry, Better Survival, and Crossbows Backport. They are not vanilla 1.12.2 items.

Divine Enchantments expands combat, exploration, and build variety with enchantments that do more than add flat numbers. Expect stacking combat momentum, positional bow play, summoned allies, size-altering curses and blessings, treasure-only blades, and deep cross-mod interactions.

Every enchantment includes **Enchantment Descriptions** support and a **JEI** information page. Treasure enchantments are clearly marked, curses have real drawbacks, and every mechanic is designed to work in **singleplayer and multiplayer** with server-authoritative logic and client synchronization.

---

## Highlights

- **71 enchantments** across weapons, bows, crossbows, armor, shields, tools, fishing rods, and boots.
- **Treasure and curse enchantments** with distinct acquisition rules.
- **Fixed enchanting table levels** for every table enchantment, with higher tiers unlocking as table power rises.
- **Average-accurate chance rolls:** a 0.4% proc is guaranteed within 250 attempts instead of relying on short random streaks.
- **Colored ancient/rune enchantments** - green runes, red voodoo, yellow ancients.
- **Custom potion effect** with a hand-drawn pixel-art icon.
- **Custom projectile, chain-hook, and bullseye rendering.**
- **Crossbow bolt support:** Spartan iron, diamond, tipped, and spectral bolts work with all crossbow enchantments.
- **Aperture only triggers while actively blocking**, not while merely holding a shield.
- **No required dependencies.** Optional integrations activate automatically when the matching mod is installed.
- **Fully configurable:** master switch, per-enchantment toggles, and balance tuning.
- **Multiplayer-aware:** inventory, durability, attributes, projectiles, status effects, and visuals are synchronized between client and server.

---

## Featured Enchantments

### Rune: Carnage
*Nunchaku, sabers, and rapiers only. Green text. Very rare table enchantment, table level 24-30.*

Every fully charged hit grants +20% attack speed per level plus +13.34% per stack, up to 7 × level stacks. At level III with 21 stacks this reaches about 4.4x the weapon base attack speed. Enemy invulnerability frames end exactly when your weapon recharges. Stacks decay by 5 every 5 seconds without a hit. Incompatible with **Swifter Slashes** from SoManyEnchantments.

### Enlargement I-II
*All melee weapons.*

Melee hits have a 2% × level chance to apply **Enlargement** for 20 seconds. The target grows by 10% × level, including its hitbox and model. Uses a custom potion effect with a custom HUD/inventory icon.

### Bullseye I-IV
*Bows.*

Hitting an enemy marks a small bullseye on them for 3 seconds. Arrows whose flight path crosses the bullseye deal an additional 20% × level damage. The marker moves with the target and is rendered as a camera-facing pixel-art target. Hitting the bullseye plays a distinct audio cue.

### Hunter I-III
*Bows.*

Arrows make the struck enemy glow for `20 × level × draw` seconds, where `draw` is the bow's charge at release. A fully drawn Level III bow marks targets for 60 seconds.

### Voodoo I-VI
*All melee weapons. Red text. Treasure only.*

Slaying an enemy has a 2% × level chance to raise a loyal zombie ally from the ground:
- `4 × level` health and 35% faster than a normal zombie.
- Deals `3 + level` damage.
- Follows its owner across dimensions and teleports when left behind.
- Attacks hostile mobs and anything that attacks its owner.
- Cannot pick up items or armor, cannot harm or be harmed by its owner.
- Red glowing outline and a `[Player]'s Ally` name tag.
- Spawns with the Zombie Villager curing sound.

### Ancient: Kratos Blades
*Sabers only. Yellow text. Treasure only.*

While the original blade is held, the other hand drops its item and receives a synchronized duplicate of the blade. Using the duplicate consumes durability from the original. Right-clicking hurls a fishing-hook chain that pulls the first mob or player it touches toward the wielder, costing exactly 1 durability. The duplicate cannot be dropped, stored, placed in containers, or used on item frames.

---

## More Enchantment Themes

- **Combat momentum:** Chainsaw, Headslicer, Culling, Dulled Impact, Stagger, Puncture.
- **Ranged play:** Autofire (60% draw minimum), Pierceful, Headhunter, Longdraw, Machine Bow, Ballistic Overdrive, Splintering Impact, Slip-Bore, Glimmer.
- **Defense and mobility:** Apex, Royal Guard, Cushioned Heel/Shirt, Mudstrider, Gargoyle Stance, Gyroscopic Plating, Grounded Vanguard.
- **Curses:** Curse of the Tether, Curse of Inertia, Curse of the Volatile Soul, Curse of the Debt of the Void, Curse of Berserker's Bloodlust.
- **Utility:** Riches, Magnetic Clasp, Thermal Foil, Desalination Mesh, Metabolic Slumber, Salvage Line, Webber.
- **Treasure hunting:** Gravitational, Aperture, Harmonic Anchor, Perpetual Crucible, Gambler's Gambit, Umbral Prowess, and more.

A full enchantment list is included in the project README.

---

## Mod Compatibility

Optional integrations activate automatically when installed:

- **Spartan Weaponry & Spartan Shields** - weapon, shield, and crossbow classification.
- **CollisionDamage** - Cushioned Shirt reduces kinetic collision damage.
- **Dynamic Surroundings** - Dampening Strike and Quiet Chipping reduce sound-based mob detection.
- **Infernal Mobs** - Suregrip blocks Sticky/Thieving disarms.
- **Crossbows** - crossbow enchantments require a 1.12.2 crossbow mod (Spartan Weaponry, Crossbows Backport, etc.).
- **Better Survival** - nunchaku support for Rune: Carnage.
- **Rin's SoManyEnchantments** - Multishot/Splitshot synergy, Advanced Mending and Swifter Slashes incompatibilities, Advanced Looting synergy.
- **Lycanites Mobs** - Ally summoning, projectile capture, Decay, Aphagia, Demon/Shadow detection, boss checks.
- **Potion Core** - Extension, Solid Core, Weight, Vulnerability, Climbing, Trueshot, and curse interactions.
- **Tough As Nails / Simple Difficulty** - temperature stabilization, clean-water infection reduction, hunger/thirst pause.
- **ItemPhysic Full** - restored walk-over item pickup.
- **JEI** - enchanted-book information pages.
- **Enchantment Descriptions** - full tooltip descriptions for every enchantment.

---

## Configuration

The generated `config/divineenchantments.cfg` includes:

- A master switch for every enchantment.
- Individual enable/disable toggles for each enchantment.
- Disabled enchantments are removed from existing items, open containers, drops, item frames, and villager offers; empty enchanted books become vanilla books.
- The server is authoritative and syncs config to clients on login and respawn.
- Balance settings for Rune: Carnage, Enlargement, Bullseye, Hunter, Voodoo, Kratos Blades, Feathered Draw, Perpetual Crucible, Autofire, Splintering Impact, Apex, Dulled Impact, Puncture, and sound-stealth radii.
- Compatibility toggles, including the Better Survival Range damage fix.
- Enchanting table ranges are documented in the project wiki: level 1-30, with treasure enchantments excluded.

Config changes require a game restart.

## Requirements

- **Minecraft:** 1.12.2
- **Loader:** Forge 14.23.5.2847+ or Cleanroom
- **Side:** Client and server (the mod must be installed on both for multiplayer)

No mandatory dependencies. JEI, Enchantment Descriptions, and the integration mods above are optional.

---

## Installation

1. Install Minecraft Forge 1.12.2 or Cleanroom.
2. Drop `DivineEnchantments-1.12.2-1.0.11.jar` into the `mods` folder.
3. Install the same jar on the server for multiplayer.
4. Launch the game.

---

## Notes

- All custom enchantments are registered normally and can be scripted, viewed in JEI, and described by Enchantment Descriptions.
- Treasure enchantments do not appear at the enchanting table unless explicitly stated.
- The mod is designed to be server-authoritative for damage, durability, AI, and inventory behavior, with client-side rendering and UI sync.
- **AI-assisted development:** Portions of the code, documentation, and assets were developed with the help of AI tools, then reviewed, tested, and integrated by the author.
- Feedback and bug reports are welcome - include your Forge/Cleanroom version, mod list, and a log.
