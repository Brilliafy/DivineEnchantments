<p align="center">
  <img src="https://raw.githubusercontent.com/Brilliafy/DivineEnchantments/main/src/main/resources/logo.png" alt="Divine Enchantments logo" width="190">
</p>

<h1 align="center">Divine Enchantments</h1>

<p align="center">
  <strong>71 handcrafted enchantments for Minecraft 1.12.2</strong><br>
  Forge and Cleanroom. Deeper combat, skill-shot archery, summoned allies,<br>
  size-altering curses and blessings, and rare rune and ancient blades.
</p>

<p align="center">
  <img alt="Minecraft 1.12.2" src="https://img.shields.io/badge/Minecraft-1.12.2-62B47A?style=for-the-badge">
  <img alt="Forge and Cleanroom" src="https://img.shields.io/badge/Loader-Forge%20%7C%20Cleanroom-E04E14?style=for-the-badge">
  <img alt="71 enchantments" src="https://img.shields.io/badge/Enchantments-71-8A2BE2?style=for-the-badge">
  <img alt="Multiplayer ready" src="https://img.shields.io/badge/Multiplayer-Ready-2EA043?style=for-the-badge">
  <img alt="GPL-3.0" src="https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge">
</p>

<p align="center">
  <a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/Enchantments.md"><strong>Enchantment Guide</strong></a>
  &nbsp;|&nbsp;
  <a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/EnchantingTable.md"><strong>Enchanting Table Guide</strong></a>
  &nbsp;|&nbsp;
  <a href="https://github.com/Brilliafy/DivineEnchantments"><strong>Source Code</strong></a>
  &nbsp;|&nbsp;
  <a href="https://github.com/Brilliafy/DivineEnchantments/issues"><strong>Report a Bug</strong></a>
</p>

---

## Start Here

<table>
<tr>
<td width="50%" valign="top">

### Full Enchantment Guide

Every enchantment, level range, acquisition route, incompatibility, and exact mechanic.

<a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/Enchantments.md"><strong>Open the Enchantment Guide</strong></a>

</td>
<td width="50%" valign="top">

### Enchanting Table Guide

Table power levels, tier unlocks, which enchantments can appear, and what stays treasure-only.

<a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/EnchantingTable.md"><strong>Open the Table Guide</strong></a>

</td>
</tr>
<tr>
<td width="50%" valign="top">

### Configuration

Master switch, per-enchantment toggles, balance values, table levels, and compatibility settings.

<a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/Configuration.md"><strong>Open Configuration</strong></a>

</td>
<td width="50%" valign="top">

### Compatibility

Spartan Weaponry, Better Survival, Crossbows Backport, Lycanites, Apotheosis, Simple Difficulty, ItemPhysic, and more.

<a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/Compatibility.md"><strong>Open Compatibility</strong></a>

</td>
</tr>
</table>

---

<details>
<summary><strong>What makes Divine Enchantments different</strong></summary>
<br>

- **71 enchantments** for weapons, bows, crossbows, armor, shields, tools, fishing rods, and boots.
- **Mechanics, not just numbers.** Momentum stacks, skill-shot bullseyes, overdraw, chain hooks, summoned allies, and size-changing effects.
- **Treasure and curse enchantments** with clear acquisition rules and real drawbacks.
- **Fixed enchanting table levels** for every table enchantment, with higher tiers unlocking as table power rises.
- **Average-accurate chance rolls.** A 0.4% proc is guaranteed within 250 attempts instead of relying on short random streaks.
- **Colored rune, voodoo, and ancient enchantments** for instant visual identity.
- **Server-authoritative.** Damage, durability, inventories, projectiles, attributes, and status effects are synced between client and server.
- **No required dependencies.** Optional integrations activate automatically when the matching mod is installed.
- **Enchantment Descriptions and JEI support** for every enchantment.

</details>

<details>
<summary><strong>Featured enchantments</strong></summary>
<br>

### Rune: Carnage
*Nunchaku, sabers, and rapiers. Green text. Very rare table enchantment.*

Fully charged hits build attack speed. Each level has a configurable max-stack multiplier. Defaults are **1.125x / 1.25x / 1.625x**, so level III matches Swifter Slashes V on a sword. Enemy invulnerability frames end exactly when your weapon recharges. Stacks decay by 5 every 5 seconds without a hit. Incompatible with **Swifter Slashes**.

### Bullseye
*Bows. Levels I-IV.*

Arrow hits can mark a small bullseye on the target. An arrow whose flight path crosses that exact mark deals bonus damage. Hitting another body part does not count. The mark is consumed by the bonus hit and a fresh one is rolled immediately. Level IV always places a new mark.

### Longdraw
*Bows. Treasure only.*

Full draw now takes **30 ticks** instead of 20, and the bow pull animation follows the longer timeline. Earlier releases fire weaker arrows. Holding past full draw overdraws up to 250% power, accelerates the arrow up to 2x speed for range, and keeps damage calculated before the acceleration. At maximum overdraw it triggers a concussive muzzle blast.

### Autofire
*Bows. Treasure only. Levels I-II.*

At **60% draw or higher**, each shot adds trailing arrows with matched velocity. Every arrow in the volley deals 60% damage. Weaker taps fire a single normal arrow. Fully compatible with Multishot.

### Voodoo
*Melee weapons. Red text. Treasure only. Levels I-VI.*

Slaying an enemy can raise a loyal zombie ally. It scales with level, follows you across dimensions, attacks hostile mobs, and cannot harm or be harmed by its owner.

### Ancient: Kratos Blades
*Sabers. Yellow text. Treasure only.*

While the original blade is held, the other hand receives a synchronized duplicate. Using the duplicate consumes durability from the original. Right-clicking hurls a chain hook that pulls the first mob or player it touches toward you.

</details>

<details>
<summary><strong>What is inside</strong></summary>
<br>

- **Combat momentum:** Chainsaw, Headslicer, Culling, Dulled Impact, Stagger, Puncture.
- **Ranged play:** Autofire, Pierceful, Headhunter, Longdraw, Machine Bow, Ballistic Overdrive, Splintering Impact, Slip-Bore, Glimmer.
- **Defense and mobility:** Apex, Royal Guard, Cushioned Heel and Shirt, Mudstrider, Gargoyle Stance, Gyroscopic Plating, Grounded Vanguard.
- **Curses:** Curse of the Tether, Curse of Inertia, Curse of the Volatile Soul, Curse of the Debt of the Void, Curse of Berserker's Bloodlust.
- **Utility:** Riches, Magnetic Clasp, Thermal Foil, Desalination Mesh, Metabolic Slumber, Salvage Line, Webber, Ground Sweep.
- **Treasure hunting:** Gravitational, Aperture, Harmonic Anchor, Perpetual Crucible, Gambler's Gambit, Umbral Prowess, and more.

The complete list, with every table range and incompatibility, lives in the <a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/Enchantments.md">Enchantment Guide</a>.

</details>

<details>
<summary><strong>Compatibility</strong></summary>
<br>

Optional integrations activate automatically when installed:

- **Spartan Weaponry and Spartan Shields** - weapon, shield, and crossbow classification.
- **Better Survival** - nunchaku support for Rune: Carnage and the Range damage fix.
- **Crossbows Backport** - crossbow and bolt support.
- **Rin's SoManyEnchantments** - Multishot and Splitshot synergy, Advanced Mending and Swifter Slashes incompatibilities, Advanced Looting synergy.
- **Lycanites Mobs** - Ally summoning, projectile capture, Decay, Aphagia, Demon and Shadow detection, boss checks.
- **Potion Core** - Extension, Solid Core, Weight, Vulnerability, Climbing, Trueshot, and curse interactions.
- **Tough As Nails and Simple Difficulty** - temperature stabilization, clean-water infection reduction, hunger and thirst pause.
- **ItemPhysic Full** - Ground Sweep works with custom pickup and keeps the vanilla 2 second drop delay.
- **CollisionDamage** - Cushioned Shirt reduces kinetic collision damage.
- **Dynamic Surroundings** - Dampening Strike and Quiet Chipping reduce sound-based mob detection.
- **Infernal Mobs** - Suregrip blocks Sticky and Thieving disarms.
- **JEI and Enchantment Descriptions** - full in-game information for every enchantment.

</details>

<details>
<summary><strong>Configuration</strong></summary>
<br>

The generated `config/divineenchantments.cfg` includes:

- A master switch and individual toggles for every enchantment.
- Disabled enchantments are stripped from existing items, containers, drops, item frames, and villager trades. Empty enchanted books become vanilla books.
- Server-authoritative config synced to clients on login and respawn.
- Balance values for Rune: Carnage, Enlargement, Bullseye, Hunter, Voodoo, Kratos Blades, Feathered Draw, Perpetual Crucible, Autofire, Splintering Impact, Apex, Dulled Impact, Puncture, and sound-stealth radii.
- Compatibility toggles, including the Better Survival Range damage fix.
- Enchanting table ranges for every table enchantment.

Config changes require a game restart. The full key list is in the <a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/Configuration.md">Configuration Guide</a>.

</details>

<details>
<summary><strong>Installation and requirements</strong></summary>
<br>

**Requirements**

- **Minecraft:** 1.12.2
- **Loader:** Forge 14.23.5.2847 or newer, or Cleanroom
- **Side:** client and server. Install the same jar on both for multiplayer.

**Installation**

1. Install Minecraft Forge 1.12.2 or Cleanroom.
2. Drop `DivineEnchantments-1.12.2-1.0.16.jar` into the `mods` folder.
3. Install the same jar on the server.
4. Launch the game.

No mandatory dependencies. JEI, Enchantment Descriptions, and the integration mods above are optional.

</details>

<details>
<summary><strong>Notes and support</strong></summary>
<br>

- All custom enchantments are registered normally. They can be scripted, viewed in JEI, and described by Enchantment Descriptions.
- Treasure enchantments do not appear at the enchanting table unless the guide says otherwise.
- The mod is server-authoritative for damage, durability, AI, and inventory behavior, with client-side rendering and UI sync.
- **AI-assisted development:** portions of the code, documentation, and assets were developed with AI tools, then reviewed, tested, and integrated by the author.
- Feedback and bug reports are welcome. Include your Forge or Cleanroom version, mod list, and log.

<p align="center">
  <a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/Enchantments.md"><strong>Enchantment Guide</strong></a>
  &nbsp;|&nbsp;
  <a href="https://github.com/Brilliafy/DivineEnchantments/blob/main/docs/EnchantingTable.md"><strong>Enchanting Table Guide</strong></a>
  &nbsp;|&nbsp;
  <a href="https://github.com/Brilliafy/DivineEnchantments/issues"><strong>Issues</strong></a>
  &nbsp;|&nbsp;
  <a href="https://github.com/Brilliafy/DivineEnchantments/releases"><strong>Releases</strong></a>
</p>

</details>
