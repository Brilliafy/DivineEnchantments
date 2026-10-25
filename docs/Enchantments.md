# Enchantment Reference

Complete list of all 71 Divine Enchantments, grouped by equipment type. For a quick scannable index, see the [README](../README.md#enchantment-library). For table unlock levels, see [Enchanting Table Levels](EnchantingTable.md).

## Contents

- [Melee Weapons](#melee-weapons) - 18 enchantments
- [Ranged](#ranged) - 15 enchantments
- [Armor & Shields](#armor-shields) - 22 enchantments
- [Tools & Utility](#tools-utility) - 7 enchantments
- [Hybrid & Universal](#hybrid-universal) - 4 enchantments
- [Curses](#curses) - 5 enchantments

---

## Melee Weapons

<a id="melee-weapons"></a>

**18 enchantments.**

<a id="chainsaw"></a>

### Chainsaw

- **Type**: Swords, axes, polearms, and blunt weapons (shovels and pickaxes are excluded)
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 5-30; tier unlocks: I at 5, II at 17, III at 29).
- **Description**: Dealing a direct melee hit causes the enemy to bleed for `(4 + n)%` of weapon damage for `3 + n` times (where `n` is the enchantment level).
  - Level I: 4 bleeds of 5% weapon damage.
  - Level II: 5 bleeds of 6% weapon damage.
  - Level III: 6 bleeds of 7% weapon damage.
  - Bleed ticks occur every 0.3s (6 ticks) and do not trigger invincibility frames, allowing follow-up attacks to land unimpeded. Spawns bleeding blood particles on each tick.

<a id="sacrificial"></a>

### Sacrificial

- **Type**: Swords, axes, polearms, and blunt weapons (shovels and pickaxes are excluded)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: When held in the main hand:
  - `+10%` Attack Damage
  - `+10%` Attack Speed
  - `+10%` Movement Speed
  - `-10%` Max Health
  - `-10%` Armor

<a id="webber"></a>

### Webber

- **Type**: Swords only
- **Max Level**: II
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Swords instantly break cobwebs on touch with zero delay. Hits have a `0.4% * level` chance to spawn a cobweb on the target and play a spider sound. Level II doubles the chance to `0.8%`.

<a id="riches"></a>

### Riches

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: II
- **Acquisition**: Enchanting Table (table level 20-30; tier unlocks: I at 20, II at 30).
- **Incompatible With**: Education (`mujmajnkraftsbettersurvival:education`).
- **SoManyEnchantments Integration**: Adept increases the Riches proc chance and the XP payout.
- **Description**: Killing a mob has a chance to trigger a loot and XP explosion:
  - Level I: 10% base chance; Level II: 20% base chance.
  - Bosses and boss-bar mobs are immune.
  - Adept adds +25% chance per level and adds +2 to the XP multiplier per level.
  - The loot multiplier is capped at 10x; Adept does not increase it.
  - Stacks additively with vanilla Looting and SoManyEnchantments Advanced Looting.

<a id="gambler-s-gambit"></a>

### Gambler's Gambit

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 30).
- **Description**: Melee hits have a 25% chance to inflict a random negative potion effect selected from vanilla, Potion Core, and any other installed mod. Bosses and boss-bar mobs are immune. The chance uses an average-accurate accumulator, so it guarantees a proc within 4 hits.

<a id="breach"></a>

### Breach

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: IV
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 16, III at 22, IV at 28).
- **Potion Core Integration**: Applies `potioncore:vulnerable`.
- **Description**: Each strike has a `(20 * level)%` chance to inflict Potion Core Vulnerability on the target for 15 seconds (300 ticks), and a `(15 * level)%` chance to inflict 10x durability damage to the victim's worn armor pieces.

<a id="demonic-edge"></a>

### Demonic Edge

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: V
- **Acquisition**: Enchanting Table (table level 5-30; tier unlocks: I at 5, II at 11, III at 17, IV at 23, V at 29).
- **Incompatible With**: Sharpness, Smite, Bane of Arthropods.
- **Lycanites Mobs Integration**: Targets Demon and Shadow tagged creatures.
- **Description**: Deals `+2.5 * level` bonus damage against any mob that naturally spawns in the Nether, including modded Nether biome spawns and structure mobs, plus Lycanites Mobs creatures with Demon type or Shadow element (Rahovart, Belph, Cacodemon, Behemoth, Amalgalich, etc.). The natural-spawn table is built once and cached for O(1) lookups.

<a id="necrotic-stasis"></a>

### Necrotic Stasis

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: II
- **Acquisition**: Enchanting Table (table level 8-30; tier unlocks: I at 8, II at 30).
- **Lycanites Mobs Integration**: Applies `lycanitesmobs:decay`.
- **Description**: Melee attacks inflict Decay for 10 seconds (200 ticks). At Level II, also completely purges and strips Regeneration and Absorption potion effects and bonus health hearts from the target.

<a id="volatile-discharge"></a>

### Volatile Discharge

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 20, III at 30).
- **Description**: Each strike builds an electric volatile charge of `(3 * level)%` on the target. Upon reaching 100% charge or higher, the entity violently detonates in a power 5 explosion (larger than a creeper).

<a id="headslicer"></a>

### Headslicer

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 20, III at 30).
- **Description**: Airborne melee strikes deal bonus damage whenever you are not on the ground, including jumps and falls:
  - Level I: +15% bonus damage.
  - Level II: +30% bonus damage.
  - Level III: +50% bonus damage.

<a id="culling"></a>

### Culling

- **Type**: Axes and Daggers
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: Deals +25% bonus damage exclusively against weakened targets whose current health is below 25% of their maximum health.

<a id="dulled-impact"></a>

### Dulled Impact

- **Type**: Spartan Weaponry blunt weapons (warhammers, hammers, clubs, maces, flails)
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 5-30; tier unlocks: I at 5, II at 17, III at 29).
- **Description**: Heavy blunt strikes daze the target, reducing their outgoing attack damage by 10% / 20% / 30% for 5 seconds (100 ticks). Does not stack.

<a id="stagger"></a>

### Stagger

- **Type**: Spartan Weaponry maces and axes (blunt weapons & axes)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: Fully charged melee hits have a 65% chance to pause a basic mob's attack windup and inflict Lycanites Paralysis for 3 seconds (60 ticks), or 1 second (20 ticks) if the target wears a helmet. Bosses and champions are immune.

<a id="puncture"></a>

### Puncture

- **Type**: Piercing weapons and Spears (Spartan Weaponry daggers, spears, pikes, lances, rapiers)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Piercing melee strikes bypass 10% of natural mob armor (e.g., treating a 10-armor mob as having 9 armor). Percentage-based bypass scales with armored targets without trivializing early mobs.


<a id="rune-carnage"></a>

### Rune: Carnage

- **Type**: Nunchaku, Sabers, and Rapiers (Spartan Weaponry, Better Survival, and modded equivalents)
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 24-30; tier unlocks: I at 24, II at 27, III at 30).
- **Incompatible With**: `somanyenchantments:swifterslashes` (Swifter Slashes).
- **Description**: Fully charged hits grant immediate **multiplicative** attack speed (`+20%` per enchantment level) plus `+13.34%` per stack, stacking up to `7 * level` times. At level III with 21 stacks this reaches roughly `4.4x` the weapon's base attack speed. There is no permanent attack speed penalty. Enemy invulnerability frames are set to exactly your weapon's recharge time, so they expire when the attack indicator is full again. Going 5 seconds without a hit removes 5 stacks. The enchantment name is displayed in green.

<a id="enlargement"></a>

### Enlargement

- **Type**: All melee weapons
- **Max Level**: II
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 30).
- **Description**: Melee hits have a `2 * level%` chance to apply **Enlargement** for 20 seconds, increasing the target's model size and hitbox by `10 * level%`, capped at 30%. Bosses and mobs above 150 HP receive half duration. The effect uses a custom pixel-art status icon and is synchronized to nearby clients.

<a id="voodoo"></a>

### Voodoo

- **Type**: Swords, axes, polearms, and blunt weapons (shovels and pickaxes excluded)
- **Max Level**: VI
- **Acquisition**: Treasure only (not available from Enchanting Table). Red enchantment name.
- **Description**: Slaying an enemy with the weapon has a `2 * level%` chance to raise a loyal zombie ally from the ground at the corpse. The ally lasts `30 * level` seconds:
  - Health: `4 * level`.
  - Movement speed: 35% faster than a normal zombie.
  - Attack damage: `3 + level`.
  - Follows its owner across dimensions and teleports when left far behind.
  - Prioritizes following its owner and only attacks visible hostile mobs within 24 blocks of the owner. It no longer chases enemies through walls or far away from the player.
  - Attacks anything that attacks its owner.
  - Cannot pick up items or armor, cannot hurt or be hurt by its owner, and has a red glowing outline.
  - Displayed name: `[Player]'s Ally`.
  - Maximum 6 living allies per player.
  - Incompatible with Ally.
  - Spawn sound: Zombie Villager curing sound.

<a id="ancient-kratos-blades"></a>

### Ancient: Kratos Blades

- **Type**: Sabers only
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table). Yellow enchantment name.
- **Description**: While the original blade is held, the other hand immediately drops whatever it is holding and receives a functional duplicate of the blade. Right-clicking hurls a fishing-rod-style chain; the first mob or player it touches is violently pulled toward the wielder, consuming exactly 1 durability from the original blade. The duplicate vanishes when the original is no longer held and cannot be dropped.

---

[Back to top](#contents)

---

## Ranged

<a id="ranged"></a>

**15 enchantments.**

<a id="gravitational"></a>

### Gravitational

- **Type**: Bows
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: When the fired arrow lands (whether on a block or an entity), it pulls all mobs within a 5-block radius 2 blocks towards the landing point with attractive motion vectors and gravitational portal effects.

<a id="pierceful"></a>

### Pierceful

- **Type**: Bows
- **Max Level**: II
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Incompatible With**: Rune: Arrow Piercing (SoManyEnchantments), Longdraw.
- **Description**: Enables arrows to pass through targets without stopping:
  - Level I: Pierces through 1 entity before landing on the next entity or block.
  - Level II: Pierces through 2 entities before landing.
  - Seamlessly triggers Gravitational when the arrow finally lands!

<a id="autofire"></a>

### Autofire

- **Type**: Bows
- **Max Level**: II
- **Acquisition**: Super rare treasure (not available from Enchanting Table).
- **Description**: Requires at least **60% draw** (12 of 20 ticks). Weaker taps fire a single normal arrow. At 60% draw or higher, trailing arrows travel immediately behind each arrow with identical velocity and trajectory. Every arrow in the volley deals 60% damage.
  - Level I: 2 arrows total (original + 1 trailing arrow).
  - Level II: 3 arrows total (original + 2 trailing arrows).
  - Removes target invincibility frames so every arrow can land.
  - Fully compatible with Multishot / Splitshot from Rin's SoManyEnchantments.

<a id="headhunter"></a>

### Headhunter

- **Type**: Bows
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Incompatible With**: Mending, Advanced Mending (from SoManyEnchantments), and Infinity.
- **Description**: Arrows that hit an enemy's head / topmost hitbox area deal `1.5x` critical damage with magic crit particles and audio feedback.

<a id="slip-bore"></a>

### Slip-Bore

- **Type**: Bows and modded crossbows
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 30).
- **Description**: Arrows punch a low-pressure vacuum tunnel (3-block radius) through the air along their flight path. Nearby entities have their velocity yanked inward toward the trajectory centerline, dragging flying modded mobs (Cacodemons, Raikos, etc.) crashing down to the ground and pulling ground mobs into a tight line behind the arrow. Incompatible with Multishot and Splitshot.

<a id="muzzle"></a>

### Muzzle

- **Type**: Crossbows (Spartan Weaponry & modded crossbows)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 30).
- **Lycanites Mobs Integration**: Applies `lycanitesmobs:aphagia`.
- **Description**: Fired crossbow projectiles afflict targets with Aphagia for 10 seconds (200 ticks), disabling item usage and consumption (eating, potions, active items). Works with Spartan iron, diamond, tipped, and spectral bolts.

<a id="archery"></a>

### Archery

- **Type**: Bows and modded crossbows
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 8-30; tier unlocks: I at 8, II at 19, III at 30).
- **Potion Core Integration**: Directly drives `potioncore:archery` (Trueshot).
- **Description**: Bow and crossbow kills grant Trueshot I for 10 seconds.
  - Level I: Killing 1 enemy grants Trueshot I for 10 seconds.
  - Level II: Killing 4 enemies within those 10 seconds upgrades the buff to Trueshot II for 10 seconds.
  - Level III: Killing 10 enemies within those 10 seconds upgrades the buff to Trueshot III for 10 seconds.

<a id="ballistic-overdrive"></a>

### Ballistic Overdrive

- **Type**: Modded crossbows
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 25-30).
- **Incompatible With**: Rapid Load, Strafe, and rapid-firing crossbow enchantments.
- **Description**: Loaded crossbow projectiles completely ignore gravity, travelling in a laser-straight vector with infinite punch-through piercing through non-boss entities until colliding with solid terrain or a boss monster. Applies to Spartan iron, diamond, tipped, and spectral bolts by resetting their vertical motion every tick.

<a id="splintering-impact"></a>

### Splintering Impact

- **Type**: Modded crossbows
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 20, III at 30).
- **Description**: When a crossbow arrow or bolt, including Spartan iron and diamond bolts, strikes a solid block instead of an entity, it shatters once into a radial burst of independent shrapnel arrows. Each shard uses half of the original bolt's base damage, so Power and Advanced Power scale the shrapnel. Shards can never splinter again, preventing chain reactions when a block is broken. Incompatible with Multishot, Splitshot, and Spreadshot.
  - Level I: 4 shrapnel arrows.
  - Level II: 9 shrapnel arrows.
  - Level III: 13 shrapnel arrows.

<a id="longdraw"></a>

### Longdraw

- **Type**: Bows
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Incompatible With**: Mending, Strafe, Multishot, Splitshot, Rapid Fire, Range, Pierceful.
- **Description**: Allows bows to be drawn past 100% up to 250% overdraw power (requiring 50 ticks of full hold). Releasing at 250% triggers a concussive muzzle blast that pierces up to 5 targets with scaled damage and recoils the archer backward 6 blocks.

<a id="glimmer"></a>

### Glimmer

- **Type**: Bows and modded crossbows
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 1-30).
- **Description**: Fired arrows and bolts leave a shimmering trail of particles and mark struck targets with the Glowing status effect for 5 seconds without inflicting bonus damage.

<a id="feathered-draw"></a>

### Feathered Draw

- **Type**: Bows
- **Max Level**: IV
- **Acquisition**: Enchanting Table (table level 8-30; tier unlocks: I at 8, II at 15, III at 22, IV at 29).
- **Incompatible With**: Strafe (SoManyEnchantments).
- **Description**: Removes 20% / 40% / 60% / 80% of the vanilla bow draw slowdown at levels I-IV, leaving -64% / -48% / -32% / -16% movement speed while aiming. The movement input is adjusted only while the bow is actively charging, so there is no persistent speed attribute and no leak on disconnect or item swap. Incompatible with Strafe.


<a id="machine-bow"></a>

### Machine Bow

- **Type**: Bows
- **Max Level**: IV
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 16, III at 22, IV at 28).
- **Description**: Reaching 100% draw power fires the arrow automatically and immediately resets draw for continuous rapid-fire bursts. Draw times are 20 / 16 / 13 / 10 ticks at levels I-IV; higher levels also reduce recoil and spread. Mutually exclusive with Multishot, Rapid Fire, Strafe, and Infinity.

<a id="bullseye"></a>

### Bullseye

- **Type**: Bows
- **Max Level**: IV
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 16, III at 22, IV at 28).
- **Description**: Each arrow hit has a `25% * level` chance to place a bullseye on the enemy for 3 seconds. Level IV always places it. An arrow whose travel ray passes through the marked area deals an additional `20 * level%` damage. Hitting a different body part does not count. The marker is consumed by the qualifying hit, so it must be placed again before another bonus can trigger. The bullseye is rendered as a camera-facing pixel-art target attached to the enemy.

<a id="hunter"></a>

### Hunter

- **Type**: Bows
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 5-30; tier unlocks: I at 5, II at 17, III at 29).
- **Description**: Arrows apply Glowing to the struck enemy for `20 * level * draw` seconds, where `draw` is the bow's charge at release (`0.0` to `1.0`). A fully drawn Level III bow marks targets for 60 seconds.

[Back to top](#contents)

---

## Armor & Shields

<a id="armor-shields"></a>

**22 enchantments.**

<a id="heavyplated"></a>

### Heavyplated

- **Type**: Any armor piece (Helmet, Chestplate, Leggings, Boots)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 5-30).
- **Description**: Each equipped armor piece with Heavyplated grants:
  - `+8%` Armor
  - `-3%` Movement Speed
  - Stacks up to `+32%` Armor and `-12%` Movement Speed with a full set.

<a id="slowsink"></a>

### Slowsink

- **Type**: Boots
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 5-30; tier unlocks: I at 5, II at 17, III at 29).
- **Description**: Grants feather-like slow falling when submerged in water, preventing rapid drowning or falling into oceanic hazards. Descent becomes progressively slower at higher levels. Sneaking allows sinking at normal speed.

<a id="apex"></a>

### Apex

- **Type**: Helmets
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Grants high-ground advantage:
  - Deals `+2%` damage per block of elevation above the target, capped at +25%.
  - Takes `+2%` damage per block below attackers, capped at +25%.

<a id="aperture"></a>

### Aperture

- **Type**: Shields (Vanilla and Spartan Shields)
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Only works while actively blocking with the shield. Captures incoming enemy projectiles (Skeleton arrows, Spartan bolts, Ice and Fire dragon breath orbs, Lycanites Cacodemon balls, Reaper spectral bolts, Ghast fireballs, etc.) upon contact. The projectile does not trigger hit logic; its entity instance is captured, ownership is fully transferred to you (including SRG thrower/shooter fields and modded target fields), it is re-aimed along your `player.getLookVec()` trajectory, given a `+50%` speed boost, and re-fired from eye height as an allied projectile. It can damage its original owner.

<a id="gyroscopic-plating"></a>

### Gyroscopic Plating

- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Sneak-based spatial stability and momentum conversion:
  - **Effect Suppression**: While sneaking, temporarily suppresses Nausea, Lycanites Instability, Potion Core Spin, Perplexity, and Inversion. The camera is never modified by this enchantment. Suppressed effects return when you stop sneaking.
  - **Momentum Slingshot**: If you fall from a height $> 4$ blocks and tap Crouch right before landing, 100% of vertical fall momentum is converted into horizontal forward velocity ($v_{\text{horizontal}} = \sqrt{2 \cdot g \cdot h}$ along your look vector), zeroing out all fall damage and slingshotting you forward.

<a id="perpetual-crucible"></a>

### Perpetual Crucible

- **Type**: Chestplates
- **Max Level**: III
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Potion Core Integration**: Directly drives `potioncore:extension`.
- **Description**: Landing critical hits or killing a mob grants the player Potion Core Extension I/II/III for 3/4.5/6 seconds (60/90/120 ticks), scaling with enchantment level. Extension pauses other potion durations from ticking down, freezing active combat buffs as long as you stay aggressively engaged in combat.

<a id="grounded-vanguard"></a>

### Grounded Vanguard

- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 28-30).
- **Potion Core Integration**: Directly drives `potioncore:solid_core` and `potioncore:weight` III.
- **Description**: Whenever you sneak, the boots immediately grant Solid Core (cancels knockback) and Weight III (heavy downward gravity vector), preventing any entity (Rocs, Raikos, Djinns) from lifting, carrying, or displacing you. Releasing sneak smoothly purges the effect without fall damage.

<a id="arachnid-ascent"></a>

### Arachnid Ascent

- **Type**: Leggings
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Potion Core Integration**: Drives `potioncore:climb` (Climbing).
- **Description**: Wearing the leggings grants the Climbing status effect, allowing the player to walk directly up vertical walls and cliffs like a spider. Automatically removed when unequipped.

<a id="umbral-prowess"></a>

### Umbral Prowess

- **Type**: Helmets
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: In deep darkness (light level 4 or below), grants the wearer innate Night Vision, +20% movement speed, and +20% physical melee damage with abyssal portal particles. However, stepping into bright light (light level 12 or above) inflicts a minor Slowness I debuff, rewarding subterranean and nocturnal hunters.

<a id="gourmand-s-vitality"></a>

### Gourmand’s Vitality

- **Type**: Chestplates
- **Max Level**: V
- **Acquisition**: Enchanting Table (table level 5-30; tier unlocks: I at 5, II at 11, III at 17, IV at 23, V at 29).
- **Incompatible With**: `somanyenchantments:strengthenedvitality`.
- **Description**: Eating any food item in combat triggers an emergency surge of survival buffs scaled by enchantment level:
  - Regeneration: `3 * level` seconds.
  - Resistance: `5 * level` seconds.
  - Absorption: `10 * level` seconds.
  - Heartier meals (hunger $\ge 7$ or saturation $\ge 0.8$) boost the potion amplifier to II.

<a id="cushioned-heel"></a>

### Cushioned Heel

- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Dampens kinetic wall-impact collision damage (Elytra fly-into-wall, explosive knockback into terrain) and minor fall damage ($\le 4.5$ blocks) by 30%.

<a id="mudstrider"></a>

### Mudstrider

- **Type**: Boots
- **Max Level**: III
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 20, III at 30).
- **Description**: Removes the movement speed penalties and drag of Soul Sand, modded mud/silt blocks, Cobwebs, and shallow water. The END-tick correction restores horizontal momentum and keeps the client and server movement in sync.

<a id="braced-pivot"></a>

### Braced Pivot

- **Type**: Shields
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: Reinforces shield pivot balance, cutting the base 100-tick shield-disable cooldown by 60% (to 40 ticks). This is percentage-based on the vanilla base value.

<a id="thermal-foil"></a>

### Thermal Foil

- **Type**: Any armor piece (Helmet, Chestplate, Leggings, Boots)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Slows body temperature swings and climate change rate in **Tough As Nails** or **Simple Difficulty** by 20% per piece worn (up to 80% reduction with a full set), stabilizing body heat in extreme environments.

<a id="desalination-mesh"></a>

### Desalination Mesh

- **Type**: Helmets
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Gives an 85% chance to prevent Parasites and Thirsty effects when drinking dirty or salt water (Tough As Nails or Simple Difficulty). Existing effects are not removed.

<a id="metabolic-slumber"></a>

### Metabolic Slumber

- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: Pauses hunger and thirst depletion while the player remains completely stationary and idle. Takes effect after 10 seconds (200 ticks) of resting.

<a id="cushioned-shirt"></a>

### Cushioned Shirt

- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Dampens horizontal kinetic impact damage by 60% when colliding with terrain while flying (Elytra), grappling, or riding at high speed. Directly supports CollisionDamage and similar kinetic damage sources.

<a id="magnetic-clasp"></a>

### Magnetic Clasp

- **Type**: Boots and Chestplates
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: Attracts dropped items within 3 blocks (6 blocks if worn on both boots and chestplate), but only if that exact item already exists in your inventory in an incomplete stack, avoiding inventory clutter.

<a id="ground-sweep"></a>

### Ground Sweep

- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Super rare treasure only (not available from Enchanting Table).
- **Description**: Restores automatic walk-over item pickup, collecting items you walk over without right-clicking or aiming. Includes a direct inventory fallback that works even when ItemPhysic or another mod blocks vanilla collision pickup.

<a id="shadow-stalker"></a>

### Shadow Stalker

- **Type**: Leggings
- **Max Level**: II
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 30).
- **Description**: Reduces the detection radius of unaware, sleeping, or idle mobs by 25% (Level I) or 50% (Level II) while sneaking, preventing accidental aggro from multi-level dungeon rooms or floor grates. Breaks immediately if the player sprints, attacks, or steps into direct line-of-sight within 6 blocks.

<a id="challenger-s-mark"></a>

### Challenger's Mark

- **Type**: Shields
- **Max Level**: II
- **Acquisition**: Enchanting Table (table level 10-30; tier unlocks: I at 10, II at 30).
- **Description**: Performing a shield bash on an enemy forces that target to lock onto the player for 3 seconds (Level I) or 6 seconds (Level II), breaking its target lock on passive pets, tamed mounts, or party members. Has a 6-second cooldown per target and provides no damage buffs; purely an aggro-redirection utility.

<a id="gargoyle-stance"></a>

### Gargoyle Stance

- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: While standing on solid blocks, applies Potion Core Weight and cancels displacement from Lycanites Djinn vortexes, Roa sharknado whirlpools, Loch Ness gravity fields, underwater whirlpools, and siren pulls. Does not protect against standard melee knockback or vertical explosions.

[Back to top](#contents)

---

## Tools & Utility

<a id="tools-utility"></a>

**7 enchantments.**

<a id="harmonic-anchor"></a>

### Harmonic Anchor

- **Type**: Fishing Rods
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Hooking a mob does not pull them to you; it drops a spectral anchor stone at the mob's feet. For the next 8 seconds (160 ticks), the mob cannot move more than 5 blocks away from the anchor coordinate. If they attempt to lunge, charge, or fly away, the anchor line pulls taut, slingshotting them backward to the center point. Bosses, boss-bar mobs, and mobs above 150 HP are immune. Treasure enchantment (cannot be obtained from the enchanting table).

<a id="sober-chisel"></a>

### Sober Chisel

- **Type**: Pickaxes
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Carefully chisels ores to reduce the chance of triggering aggressive ore-spawn entities (like Lycanites Mobs Geonachs from coal/iron or Tremors from stone) by 75%, at the cost of a minor -5% mining speed penalty.

<a id="shimmered"></a>

### Shimmered

- **Type**: Pickaxes
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 3-30).
- **Description**: Breaking an ore block draws a glowing through-wall box outline around identical ore blocks within 2 blocks for 1 second, revealing hidden adjacent veins.

<a id="silt-sifter"></a>

### Silt Sifter

- **Type**: Shovels
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 1-30).
- **Description**: Digging natural gravel, clay, sand, or dirt has a 5% chance to sift out minor foraging bits (flint, stray string, clay balls, or iron nuggets).

<a id="dampening-strike"></a>

### Dampening Strike

- **Type**: Pickaxes, Axes, and Hammers
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Mining blocks produces 70% less vibration and acoustic sound radius. Integrates with Dynamic Surroundings and sound-based mob targeting, preventing distant mobs from locking onto you through cave walls.

<a id="quiet-chipping"></a>

### Quiet Chipping

- **Type**: Pickaxes, Axes, and Hammers
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 8-30).
- **Description**: Reduces block break volume by 75% and cuts the acoustic vibration range by 75%. Works with axes, pickaxes, and hammers, plus Dynamic Surroundings and sound-based mob targeting.

<a id="salvage-line"></a>

### Salvage Line

- **Type**: Fishing Rods
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 10-30).
- **Description**: Catching non-fish junk items (leather boots, rusted tools, string, bones) automatically breaks them down into bonus experience points and useful repair materials (nuggets, leather, string, bone meal).

[Back to top](#contents)

---

## Hybrid & Universal

<a id="hybrid-universal"></a>

**4 enchantments.**

<a id="ally"></a>

### Ally

- **Type**: Weapons (Melee weapons & Bows)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 20-30).
- **Condition**: Only registered and available if **Lycanites Mobs** is loaded.
- **Description**: Has a 2% chance on hit to summon a friendly ephemeral Aegis minion from Lycanites Mobs. Maximum 4 living Aegis per player across all Ally weapons. Incompatible with Voodoo.
  - The Aegis is owned by the player and lasts 60 seconds (ephemeral duration identical to the Lycanites summoning staff).
  - Built-in **Assist** stance: follows the player and aggressively targets anything that attacks the player or anything the player hits.
  - Maximum 4 living Aegis per player, counted across every Ally weapon.
  - Incompatible with Voodoo.

<a id="royal-guard"></a>

### Royal Guard

- **Type**: Weapons & tools (except bows)
- **Max Level**: III
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: When taking damage, redirects `(10 * level)%` (10%, 20%, 30%) of durability damage that would be dealt to worn armor onto the durability of the held weapon or tool instead. Accompanied by a shield block sound effect.

<a id="suregrip"></a>

### Suregrip

- **Type**: Weapons and Tools
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Completely locks the held weapon or tool in the player's grasp, preventing disarms, drops, and item theft from Infernal Sticky affixes and creature disarm abilities. Due to the two-handed white-knuckle grip, holding any item in the offhand forces the offhand item to drop immediately.

<a id="tempered-core"></a>

### Tempered Core

- **Type**: Weapons and Tools (Swords, Axes, Pickaxes, Shovels, Hoes)
- **Max Level**: I
- **Acquisition**: Enchanting Table (table level 1-30).
- **Description**: Eliminates the extra durability penalty for using the wrong tool on a block or mob (e.g., hitting a zombie with a pickaxe or slicing cobwebs with a sword only consumes 1 durability instead of 2).

[Back to top](#contents)

---

## Curses

<a id="curses"></a>

**5 enchantments.**

<a id="curse-of-the-tether"></a>

### Curse of the Tether

- **Type**: Chestplates
- **Max Level**: I (Curse)
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: You take `10%` less damage from all attacks and `20%` less durability damage to your chestplate, but the chestplate cannot be removed or dropped from your chest in Survival mode. Inventory removal attempts are rejected and duplicate cursor/container copies are forcibly purged.

<a id="curse-of-inertia"></a>

### Curse of Inertia

- **Type**: Leggings
- **Max Level**: I (Curse)
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Continuous sprinting increases movement speed linearly up to `+80%` over 5 seconds. Turning radius while sprinting with momentum is severely restricted, and stopping incurs 1.5 seconds (30 ticks) of ice-like sliding deceleration across all block surfaces.

<a id="curse-of-the-volatile-soul"></a>

### Curse of the Volatile Soul

- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Treasure only (Expensive super rare, min level 30, not available from Enchanting Table).
- **Curse**: Yes.
- **Description**: Acts as an explosive Totem of Undying upon death. When the player takes fatal damage:
  - Consumes the chestplate completely.
  - Revives the player with 4 HP (2 hearts), clears negative potion effects, and grants Regeneration II (45s), Absorption II (5s), Fire Resistance (40s), and Resistance V (5s).
  - Triggers the official vanilla Totem of Undying visual animation (`byte 35`) and sound effects.
  - Detonates an apocalyptic **power 20 explosion** (`world.newExplosion`) that obliterates mobs and ruins surrounding terrain with zero damage or knockback dealt to the wearer.

<a id="curse-of-the-debt-of-the-void"></a>

### Curse of the Debt of the Void

- **Type**: All Equipment (Weapons, Tools, Armor, Shields, Bows, modded crossbows)
- **Max Level**: I
- **Curse**: Yes.
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Prevents the cursed item from ever losing durability. In exchange, every point of durability damage the item would have suffered is converted directly into hunger depletion ($1.5\times$ exhaustion) and minor true void damage ($0.25\times$ unmitigated damage) dealt to the wearer.

<a id="curse-of-berserker-s-bloodlust"></a>

### Curse of Berserker’s Bloodlust

- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: I
- **Curse**: Yes.
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Base attack damage scales up to +150% higher as the wielder's health drops toward death. In exchange, all incoming healing is reduced by 75%, including natural regeneration, potions, lifesteal, and direct health restoration.

[Back to top](#contents)

---
