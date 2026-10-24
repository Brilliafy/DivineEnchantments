<p align="center">
  <img src="src/main/resources/logo.png" alt="Divine Enchantments logo" width="180">
</p>

<h1 align="center">Divine Enchantments</h1>

<p align="center">
  <b>73 handcrafted enchantments for Minecraft 1.12.2 - Forge &amp; Cleanroom.</b><br>
  Deeper combat, skill-shot archery, summoned allies, and rare rune and ancient blades.
</p>

<p align="center">
  <img alt="Minecraft 1.12.2" src="https://img.shields.io/badge/Minecraft-1.12.2-62B47A?style=for-the-badge">
  <img alt="Forge / Cleanroom" src="https://img.shields.io/badge/Loader-Forge%20%7C%20Cleanroom-E04E14?style=for-the-badge">
  <img alt="73 enchantments" src="https://img.shields.io/badge/Enchantments-73-8A2BE2?style=for-the-badge">
  <img alt="Multiplayer ready" src="https://img.shields.io/badge/Multiplayer-Ready-2EA043?style=for-the-badge">
  <img alt="License GPL-3.0" src="https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge">
</p>

<p align="center">
  <a href="#quick-start">Quick Start</a> ·
  <a href="#featured-enchantments">Featured</a> ·
  <a href="#enchantment-library">Library</a> ·
  <a href="#compatibility">Compatibility</a> ·
  <a href="#configuration">Configuration</a> ·
  <a href="#building-from-source">Building</a>
</p>

---

## What is Divine Enchantments?

**Divine Enchantments** is a Forge / Cleanroom mod for **Minecraft 1.12.2** that adds **73** handcrafted enchantments. Instead of flat stat bonuses, it focuses on mechanics that change how you fight and move:

- Fully charged melee hits that build momentum.
- Skill-shot archery with moving bullseyes.
- Summoned allies that follow you across dimensions.
- Size-altering effects with real hitbox changes.
- Treasure-only runes, ancients, and curses with meaningful trade-offs.
- Cross-mod synergy with Spartan Weaponry, SoManyEnchantments, Lycanites Mobs, Potion Core, and more.

Every enchantment has **JEI** information and **Enchantment Descriptions** support.

---

## Quick Start

| | |
| :-- | :-- |
| **Minecraft** | 1.12.2 |
| **Loader** | Forge 14.23.5.2847+ or Cleanroom |
| **Enchantments** | 73 |
| **Required dependencies** | None |
| **Multiplayer** | Client and server |

**Install**

1. Install Minecraft Forge 1.12.2 or Cleanroom.
2. Drop `DivineEnchantments-1.12.2-1.0.4.jar` into `mods/`.
3. Install the same jar on the server for multiplayer.
4. Launch the game.

> **1.12.2 weapon note:** Maces, sabers, rapiers, spears, pikes, lances, glaives, halberds, nunchaku, and crossbows come from 1.12.2 weapon mods such as **Spartan Weaponry**, **Better Survival**, and **Crossbows Backport**. They are not vanilla 1.12.2 items.

---

## Featured Enchantments

<details open>
<summary><b>Six standout enchantments from the latest batch</b></summary>

| Enchantment | Type | Max | What it does |
| :-- | :-- | :-- | :-- |
| **[Rune: Carnage](docs/Enchantments.md#rune-carnage)** | Nunchaku / Saber / Rapier | III | Full-charge hits build multiplicative attack speed to about 2.2x baseline at III; enemy i-frames end exactly when your weapon recharges. |
| **[Enlargement](docs/Enchantments.md#enlargement)** | Melee | II | 2% × level chance to grow the target by 10% × level for 20 seconds, capped at 30%; bosses/HP>150 get half duration. |
| **[Bullseye](docs/Enchantments.md#bullseye)** | Bow | IV | Marks a 3-second bullseye; arrows through it deal +20% × level damage. |
| **[Hunter](docs/Enchantments.md#hunter)** | Bow | III | Glowing for 20 × level × draw seconds after an arrow hit. |
| **[Voodoo](docs/Enchantments.md#voodoo)** | Melee | VI | Kills can raise a scaling zombie ally that follows you across dimensions; max 6 allies. |
| **[Ancient: Kratos Blades](docs/Enchantments.md#ancient-kratos-blades)** | Saber | I | Duplicates the blade into the other hand; shared durability and a right-click chain pull. |

</details>

---

## Enchantment Library

<details open>
<summary><b>How to read the tables</b></summary>

- **Source:** `Table` = enchanting table. `Lv X-Y` is the table power range where the enchantment can appear. `Treasure` = loot/trade only.
- **Max:** maximum enchantment level.
- **Full details:** every entry opens in the [complete enchantment reference](docs/Enchantments.md).
- **Table levels:** see [Enchanting Table Levels](docs/EnchantingTable.md) for every first level and tier unlock.

</details>


<details>
<summary><b>⚔️ Melee Weapons</b> - 19 enchantments</summary>

| Enchantment | Type | Max | Source | Effect |
| :-- | :-- | :-- | :-- | :-- |
| [Chainsaw](docs/Enchantments.md#chainsaw) | Melee | III | Table · Lv 5-30 | Dealing a direct melee hit causes the enemy to bleed for (4 + n)% of weapon damage for 3 + n times (where n is the enchantment level). |
| [Sacrificial](docs/Enchantments.md#sacrificial) | Melee | I | Table · Lv 10-30 | When held: +10% damage, attack speed, and movement speed; -10% max health and armor. |
| [Webber](docs/Enchantments.md#webber) | Melee | I | Treasure | Swords instantly break cobwebs on touch with zero delay, and hits have a 0.4% chance to spawn a cobweb on the target. |
| [Riches](docs/Enchantments.md#riches) | Melee | II | Table · Lv 20-30 | 0.1% / 0.2% kill proc for 10x loot and boosted XP; Adept increases chance/XP. Incompatible with Education. |
| [Gambler's Gambit](docs/Enchantments.md#gambler-s-gambit) | Melee | I | Table · Lv 30 | Inflicts a random negative potion effect from vanilla, Potion Core, or any installed mod. |
| [Breach](docs/Enchantments.md#breach) | Melee | IV | Table · Lv 10-30 | Each strike has a (20 * level)% chance to inflict Potion Core Vulnerability on the target for 15 seconds (300 ticks), and a (15 * level)% chance to… |
| [Demonic Edge](docs/Enchantments.md#demonic-edge) | Melee | V | Table · Lv 5-30 | +2.5 damage per level against any mob that naturally spawns in the Nether, including modded Nether spawns. |
| [Necrotic Stasis](docs/Enchantments.md#necrotic-stasis) | Melee | II | Table · Lv 8-30 | Melee attacks inflict Decay for 10 seconds (200 ticks). At Level II, also completely purges and strips Regeneration and Absorption potion effects and… |
| [Volatile Discharge](docs/Enchantments.md#volatile-discharge) | Melee | III | Table · Lv 10-30 | Each strike builds an electric volatile charge of (3 * level)% on the target. Upon reaching 100% charge or higher, the entity violently detonates in… |
| [Headslicer](docs/Enchantments.md#headslicer) | Melee | III | Table · Lv 10-30 | Airborne hits near jump apex deal +15% / +30% / +50% damage. |
| [Culling](docs/Enchantments.md#culling) | Melee | I | Table · Lv 10-30 | Deals +25% bonus damage exclusively against targets below 25% of their maximum health. |
| [Dulled Impact](docs/Enchantments.md#dulled-impact) | Melee | III | Table · Lv 5-30 | Reduces outgoing attack damage by 10% / 20% / 30% for 2 seconds. |
| [Stagger](docs/Enchantments.md#stagger) | Melee | I | Table · Lv 10-30 | Melee strikes briefly pause a basic mob's attack windup animation by 0.5 seconds (10 ticks), interrupting an immediate counter-swing. Bosses and… |
| [Puncture](docs/Enchantments.md#puncture) | Melee | I | Table · Lv 8-30 | Bypasses 10% of natural mob armor. |
| [Low Sweep](docs/Enchantments.md#low-sweep) | Melee | III | Table · Lv 5-30 | Sweeping strikes prioritize ankle-height hitboxes, applying a 0.5-second stagger to ground-crawling or short mobs (Silverfish, small spiders, hounds)… |
| [Rune: Carnage](docs/Enchantments.md#rune-carnage) | Melee | III | Table · Lv 24-30 | Full-charge hits build multiplicative attack speed, reaching about 2.2x baseline at III. Enemy i-frames end exactly when your weapon recharges. |
| [Enlargement](docs/Enchantments.md#enlargement) | Melee | II | Table · Lv 10-30 | 2%/level chance to grow a target by 10%/level for 20s, capped at 30%. Bosses/HP>150 get half duration. |
| [Voodoo](docs/Enchantments.md#voodoo) | Melee | VI | Treasure | Kills can raise a scaling zombie ally; max 6 allies per player. |
| [Ancient: Kratos Blades](docs/Enchantments.md#ancient-kratos-blades) | Melee | I | Treasure | While the original blade is held, the other hand immediately drops whatever it is holding and receives a functional duplicate of the blade.… |

</details>

<details>
<summary><b>🏹 Ranged</b> - 16 enchantments</summary>

| Enchantment | Type | Max | Source | Effect |
| :-- | :-- | :-- | :-- | :-- |
| [Gravitational](docs/Enchantments.md#gravitational) | Bow | I | Treasure | When the fired arrow lands (whether on a block or an entity), it pulls all mobs within a 5-block radius 2 blocks towards the landing point with… |
| [Pierceful](docs/Enchantments.md#pierceful) | Bow | II | Treasure | Arrows pierce 1 / 2 entities before landing. |
| [Autofire](docs/Enchantments.md#autofire) | Bow | II | Treasure · Super rare | Fires 1 / 2 trailing arrows with matched velocity. Each arrow deals 60% damage. |
| [Headhunter](docs/Enchantments.md#headhunter) | Bow | I | Treasure | Arrows that hit an enemy's head / topmost hitbox area deal 1.5x critical damage with magic crit particles and audio feedback. |
| [Slip-Bore](docs/Enchantments.md#slip-bore) | Bow / Crossbow | I | Table · Lv 30 | Arrows punch a low-pressure vacuum tunnel (3-block radius) through the air along their flight path. Nearby entities have their velocity yanked inward… |
| [Muzzle](docs/Enchantments.md#muzzle) | Bow / Crossbow | I | Table · Lv 30 | Fired crossbow projectiles afflict targets with Aphagia for 10 seconds (200 ticks), disabling item usage and consumption (eating, potions, active… |
| [Archery](docs/Enchantments.md#archery) | Bow / Crossbow | III | Table · Lv 8-30 | Bow and crossbow kills grant Trueshot I for 10 seconds. |
| [Ballistic Overdrive](docs/Enchantments.md#ballistic-overdrive) | Bow / Crossbow | I | Table · Lv 25-30 | Loaded crossbow projectiles completely ignore gravity (noGravity = true), travelling in a laser-straight horizontal vector with infinite… |
| [Splintering Impact](docs/Enchantments.md#splintering-impact) | Bow / Crossbow | III | Table · Lv 10-30 | Block hits burst into 4 / 9 / 13 radial shrapnel arrows using the bolt's base damage. |
| [Longdraw](docs/Enchantments.md#longdraw) | Bow | I | Treasure | Allows bows to be drawn past 100% up to 250% overdraw power (requiring 50 ticks of full hold). Releasing at 250% triggers a concussive muzzle blast… |
| [Glimmer](docs/Enchantments.md#glimmer) | Bow / Crossbow | I | Table · Lv 1-30 | Fired arrows and bolts leave a shimmering trail of particles and mark struck targets with the Glowing status effect for 5 seconds without inflicting… |
| [Feathered Draw](docs/Enchantments.md#feathered-draw) | Bow | IV | Table · Lv 8-30 | Removes 20% / 40% / 60% / 80% of bow draw slowdown (-64% / -48% / -32% / -16% remaining). Incompatible with Strafe. |
| [Scavenger's Habit](docs/Enchantments.md#scavenger-s-habit) | Bow / Thrown | IV | Table · Lv 8-30 | Killing an enemy with an arrow or thrown weapon has a (25 * level)% chance (up to 100% at Level IV) to refund the projectile directly into the… |
| [Machine Bow](docs/Enchantments.md#machine-bow) | Bow | IV | Table · Lv 10-30 | Auto-fires at full draw; draw times 20 / 18 / 16 / 14 ticks at levels I-IV. |
| [Bullseye](docs/Enchantments.md#bullseye) | Bow | IV | Table · Lv 10-30 | Hitting an enemy with an arrow marks a small bullseye on them for 3 seconds. Arrows whose flight path passes through the bullseye deal an additional… |
| [Hunter](docs/Enchantments.md#hunter) | Bow | III | Table · Lv 5-30 | Arrows apply Glowing to the struck enemy for 20 * level * draw seconds, where draw is the bow's charge at release (0.0 to 1.0). A fully drawn Level… |

</details>

<details>
<summary><b>🛡️ Armor & Shields</b> - 22 enchantments</summary>

| Enchantment | Type | Max | Source | Effect |
| :-- | :-- | :-- | :-- | :-- |
| [Heavyplated](docs/Enchantments.md#heavyplated) | Armor | I | Table · Lv 5-30 | +8% armor and -3% movement speed per piece. |
| [Slowsink](docs/Enchantments.md#slowsink) | Boots | III | Table · Lv 5-30 | Grants feather-like slow falling when submerged in water, preventing rapid drowning or falling into oceanic hazards. Descent becomes progressively… |
| [Apex](docs/Enchantments.md#apex) | Helmet | I | Treasure | +2% damage per block above the target, capped at +25%; +2% taken per block below, capped at 25%. |
| [Aperture](docs/Enchantments.md#aperture) | Shield | I | Treasure | Captures incoming enemy projectiles (Skeleton arrows, Ice and Fire dragon breath orbs, Lycanites Cacodemon balls, Ghast fireballs, etc.) upon contact… |
| [Gyroscopic Plating](docs/Enchantments.md#gyroscopic-plating) | Chestplate | I | Treasure | While sneaking: camera immunity to spin/inversion/tilt (effects remain). Fall-momentum slingshot. |
| [Perpetual Crucible](docs/Enchantments.md#perpetual-crucible) | Chestplate | III | Treasure | Crits and kills grant Extension I/II/III for 3/4.5/6 seconds. |
| [Grounded Vanguard](docs/Enchantments.md#grounded-vanguard) | Boots | I | Table · Lv 28-30 | Whenever you sneak, the boots immediately grant Solid Core (cancels knockback) and Weight III (heavy downward gravity vector), preventing any entity… |
| [Arachnid Ascent](docs/Enchantments.md#arachnid-ascent) | Leggings | I | Treasure | Wearing the leggings grants the Climbing status effect, allowing the player to walk directly up vertical walls and cliffs like a spider.… |
| [Umbral Prowess](docs/Enchantments.md#umbral-prowess) | Helmet | I | Treasure | In deep darkness (light level 4 or below), grants the wearer innate Night Vision, +20% movement speed, and +20% physical melee damage with abyssal… |
| [Gourmand’s Vitality](docs/Enchantments.md#gourmand-s-vitality) | Chestplate | V | Table · Lv 5-30 | Eating grants regeneration, resistance, and absorption. |
| [Cushioned Heel](docs/Enchantments.md#cushioned-heel) | Boots | I | Table · Lv 8-30 | Dampens kinetic wall-impact collision damage (Elytra fly-into-wall, explosive knockback into terrain) and minor fall damage (\le 4.5 blocks) by 30%. |
| [Mudstrider](docs/Enchantments.md#mudstrider) | Boots | III | Table · Lv 10-30 | Negates the movement speed penalties and drag of non-solid friction blocks (Soul Sand, modded mud/silt blocks, Cobwebs, and shallow water) while… |
| [Braced Pivot](docs/Enchantments.md#braced-pivot) | Shield | I | Table · Lv 10-30 | Reinforces shield pivot balance, shortening the shield-disable stun timer from heavy axe strikes and smashes by 1 second (cooldown reduced by 20… |
| [Thermal Foil](docs/Enchantments.md#thermal-foil) | Armor | I | Table · Lv 8-30 | Slows body temperature swings and climate change rate in Tough As Nails or Simple Difficulty by 20% per piece worn (up to 80% reduction with a full… |
| [Desalination Mesh](docs/Enchantments.md#desalination-mesh) | Helmet | I | Table · Lv 8-30 | Reduces infection chance from drinking dirty or salt water (such as Parasites in Tough As Nails or Simple Difficulty) by 85% when drinking directly… |
| [Metabolic Slumber](docs/Enchantments.md#metabolic-slumber) | Chestplate | I | Table · Lv 10-30 | Pauses hunger and thirst depletion while the player remains completely stationary and idle. Takes effect after 10 seconds (200 ticks) of resting. |
| [Cushioned Shirt](docs/Enchantments.md#cushioned-shirt) | Chestplate | I | Table · Lv 8-30 | Dampens horizontal kinetic impact damage by 60% when colliding with terrain while flying (Elytra), grappling, or riding at high speed. |
| [Magnetic Clasp](docs/Enchantments.md#magnetic-clasp) | Boots / Chestplate | I | Table · Lv 10-30 | Attracts dropped items within 3 blocks (6 blocks if worn on both boots and chestplate), but only if that exact item already exists in your inventory… |
| [Ground Sweep](docs/Enchantments.md#ground-sweep) | Boots | I | Treasure · Super rare | Restores vanilla automatic walk-over collision item pickup, collecting items you walk over without requiring manual right-clicking or aiming… |
| [Shadow Stalker](docs/Enchantments.md#shadow-stalker) | Leggings | II | Table · Lv 10-30 | Reduces the detection radius of unaware, sleeping, or idle mobs by 25% (Level I) or 50% (Level II) while sneaking, preventing accidental aggro from… |
| [Challenger's Mark](docs/Enchantments.md#challenger-s-mark) | Shield | II | Table · Lv 10-30 | Performing a shield bash on an enemy forces that target to lock onto the player for 3 seconds (Level I) or 6 seconds (Level II), breaking its target… |
| [Gargoyle Stance](docs/Enchantments.md#gargoyle-stance) | Boots | I | Table · Lv 10-30 | Eliminates involuntary displacement from external suction and vacuum effects (such as Lycanites Djinn vortexes, underwater whirlpools, or siren… |

</details>

<details>
<summary><b>⛏️ Tools & Utility</b> - 7 enchantments</summary>

| Enchantment | Type | Max | Source | Effect |
| :-- | :-- | :-- | :-- | :-- |
| [Harmonic Anchor](docs/Enchantments.md#harmonic-anchor) | Fishing Rod | I | Treasure | Anchors a mob for 8 seconds; bosses, boss-bar mobs, and HP>150 are immune. |
| [Sober Chisel](docs/Enchantments.md#sober-chisel) | Pickaxe | I | Table · Lv 8-30 | Carefully chisels ores to reduce the chance of triggering aggressive ore-spawn entities (like Lycanites Mobs Geonachs from coal/iron or Tremors from… |
| [Shimmered](docs/Enchantments.md#shimmered) | Pickaxe | I | Table · Lv 3-30 | Breaking an ore block highlights directly adjacent identical ore blocks within a 2-block radius with a brief 1-second particle shimmer, pinpointing… |
| [Silt Sifter](docs/Enchantments.md#silt-sifter) | Shovel | I | Table · Lv 1-30 | Digging natural gravel, clay, sand, or dirt has a 5% chance to sift out minor foraging bits (flint, stray string, clay balls, or iron nuggets). |
| [Dampening Strike](docs/Enchantments.md#dampening-strike) | Pickaxe / Hammer | I | Table · Lv 8-30 | 70% quieter mining; supports Dynamic Surroundings and sound-based mob targeting. |
| [Quiet Chipping](docs/Enchantments.md#quiet-chipping) | Pickaxe / Hammer | I | Table · Lv 8-30 | 75% smaller acoustic alert radius; supports sound-based mob targeting. |
| [Salvage Line](docs/Enchantments.md#salvage-line) | Fishing Rod | I | Table · Lv 10-30 | Catching non-fish junk items (leather boots, rusted tools, string, bones) automatically breaks them down into bonus experience points and useful… |

</details>

<details>
<summary><b>🧩 Hybrid & Universal</b> - 4 enchantments</summary>

| Enchantment | Type | Max | Source | Effect |
| :-- | :-- | :-- | :-- | :-- |
| [Ally](docs/Enchantments.md#ally) | Melee / Bow | I | Table · Lv 20-30 | Has a 2% chance on hit to summon a friendly ephemeral Aegis minion from Lycanites Mobs. |
| [Royal Guard](docs/Enchantments.md#royal-guard) | Weapon / Tool | III | Treasure | When taking damage, redirects (10 * level)% (10%, 20%, 30%) of durability damage that would be dealt to worn armor onto the durability of the held… |
| [Suregrip](docs/Enchantments.md#suregrip) | Weapon / Tool | I | Treasure | Locks the held item in place and blocks disarm/theft, including Infernal Mobs Sticky. |
| [Tempered Core](docs/Enchantments.md#tempered-core) | Weapon / Tool | I | Table · Lv 1-30 | Eliminates the extra durability penalty for using the wrong tool on a block or mob (e.g., hitting a zombie with a pickaxe or slicing cobwebs with a… |

</details>

<details>
<summary><b>☠️ Curses</b> - 5 enchantments</summary>

| Enchantment | Type | Max | Source | Effect |
| :-- | :-- | :-- | :-- | :-- |
| [Curse of the Tether](docs/Enchantments.md#curse-of-the-tether) | Chestplate | I (Curse) | Treasure | You take 10% less damage from all attacks and 20% less durability damage to your chestplate, but the chestplate cannot be removed or dropped from… |
| [Curse of Inertia](docs/Enchantments.md#curse-of-inertia) | Leggings | I (Curse) | Treasure | Continuous sprinting increases movement speed linearly up to +80% over 5 seconds. Turning radius while sprinting with momentum is severely… |
| [Curse of the Volatile Soul](docs/Enchantments.md#curse-of-the-volatile-soul) | Chestplate | I | Treasure · Lv 30 · Super rare | Explosive Totem of Undying; consumes the chestplate. |
| [Curse of the Debt of the Void](docs/Enchantments.md#curse-of-the-debt-of-the-void) | All gear | I | Treasure | Prevents the cursed item from ever losing durability. In exchange, every point of durability damage the item would have suffered is converted… |
| [Curse of Berserker’s Bloodlust](docs/Enchantments.md#curse-of-berserker-s-bloodlust) | Melee | I | Treasure | Base attack damage scales up to +150% higher as the wielder's health drops toward death. In exchange, incoming healing from natural regeneration,… |

</details>

---

## Compatibility

<details>
<summary><b>Optional integrations</b> - the mod runs fine without them</summary>

- **Spartan Weaponry & Spartan Shields** - weapon, shield, and modded-crossbow classification.
- **Better Survival** - nunchaku support for Rune: Carnage, Education incompatibility, and fixed Range arrow damage.
- **Rin's SoManyEnchantments** - Adept synergy for Riches, Multishot/Splitshot synergy, Advanced Mending/Swifter Slashes/Education/Pierceful/Strafe incompatibilities, Advanced Looting synergy.
- **Lycanites Mobs** - Ally summoning, projectile capture, Decay, Aphagia, Demon/Shadow detection, boss checks.
- **Potion Core** - Extension, Solid Core, Weight, Vulnerability, Climbing, Trueshot, and a dynamic negative-effect pool.
- **Tough As Nails / Simple Difficulty** - temperature stabilization, clean-water infection reduction, hunger/thirst pause.
- **ItemPhysic Full** - restored walk-over item pickup.
- **CollisionDamage** - Cushioned Shirt handles kinetic collision damage.
- **Dynamic Surroundings** - Dampening Strike and Quiet Chipping reduce sound-based mob detection.
- **Infernal Mobs** - Suregrip blocks Sticky/Thieving disarms.
- **JEI** - enchanted-book information pages.
- **Enchantment Descriptions** - full tooltip descriptions, including colored rune/ancient names.

[Read the full compatibility notes →](docs/Compatibility.md)

</details>

---

## Multiplayer

<details>
<summary><b>Server-authoritative and client-synced</b></summary>

- Damage, durability, AI, inventory changes, attribute modifiers, and projectile hits are handled on the **server**.
- Bullseye markers, Enlargement scaling, and Rune: Carnage stack/attack-speed state use dedicated sync packets.
- Mobs, allies, chains, team colors, custom names, and visual effects use vanilla entity/scoreboard synchronization where possible.
- The server sends its config to clients on login and respawn; the server is authoritative.
- Disabled enchantments are stripped from existing items, containers, drops, item frames, and villager offers. Empty enchanted books become vanilla books.
- Install the **same jar on both client and server**. A vanilla client cannot see the custom enchantments.

</details>

---

## Configuration

<details>
<summary><b>Config file and toggles</b></summary>

The config is created at:

```text
config/divineenchantments.cfg
```

- `enableAllEnchantments` is a master switch.
- Every enchantment has its own enable/disable toggle.
- Balance values cover Rune: Carnage, Enlargement, Bullseye, Hunter, Voodoo, Kratos Blades, Feathered Draw, Perpetual Crucible, Autofire, Splintering Impact, Apex, Dulled Impact, Puncture, and sound-stealth radii.
- Compatibility fixes include Better Survival Range damage correction.
- Disabled enchantments cannot be applied and their effects stop working.
- Existing disabled enchantments are stripped from items; empty enchanted books become vanilla books.
- The server is authoritative and syncs its config to clients on login and respawn.
- Changes require a game restart.

[Read the full configuration reference](docs/Configuration.md)

</details>

---

## Building from Source

<details>
<summary><b>Gradle build instructions</b></summary>

**Requirements:** JDK 8, Gradle wrapper (included).

```bash
./gradlew build
```

The jar is written to:

```text
build/libs/DivineEnchantments-1.12.2-1.0.4.jar
```

See [docs/Development.md](docs/Development.md) for the project layout and development notes.

</details>

---

## License

Divine Enchantments is licensed under the [GNU General Public License v3.0](LICENSE).

---

<p align="center">
  <sub>Divine Enchantments · 73 enchantments · Minecraft 1.12.2 · Forge / Cleanroom</sub>
</p>
