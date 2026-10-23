# Divine Enchantments

A Minecraft 1.12.2 Forge / Cleanroom mod adding 73 unique divine enchantments with deep mechanics, cross-mod integrations (Potion Core, Spartan Weaponry & Shields, Lycanites Mobs, Rin's SoManyEnchantments, Better Survival, Tough As Nails, Simple Difficulty, ItemPhysic), and built-in JEI & Enchantment Descriptions support.

## Enchantments

### 1. Gravitational
- **Type**: Bows
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: When the fired arrow lands (whether on a block or an entity), it pulls all mobs within a 5-block radius 2 blocks towards the landing point with attractive motion vectors and gravitational portal effects.

### 2. Chainsaw
- **Type**: Melee weapons (Swords, Axes, and Spartan Weaponry melee weapons)
- **Max Level**: III
- **Description**: Dealing a direct melee hit causes the enemy to bleed for `(4 + n)%` of weapon damage for `3 + n` times (where `n` is the enchantment level).
  - Level I: 4 bleeds of 5% weapon damage.
  - Level II: 5 bleeds of 6% weapon damage.
  - Level III: 6 bleeds of 7% weapon damage.
  - Bleed ticks occur every 0.3s (6 ticks) and do not trigger invincibility frames, allowing follow-up attacks to land unimpeded. Spawns bleeding blood particles on each tick.

### 3. Pierceful
- **Type**: Bows
- **Max Level**: II
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Enables arrows to pass through targets without stopping:
  - Level I: Pierces through 1 entity before landing on the next entity or block.
  - Level II: Pierces through 2 entities before landing.
  - Seamlessly triggers Gravitational when the arrow finally lands!

### 4. Ally
- **Type**: Weapons (Melee weapons & Bows)
- **Max Level**: I
- **Condition**: Only registered and available if **Lycanites Mobs** is loaded.
- **Description**: Has a 2% chance on hit to summon a friendly ephemeral Aegis minion from Lycanites Mobs.
  - The Aegis is owned by the player and lasts 60 seconds (ephemeral duration identical to the Lycanites summoning staff).
  - Built-in **Assist** stance: follows the player and aggressively targets anything that attacks the player or anything the player hits.

### 5. Sacrificial
- **Type**: Melee weapons (Swords, Axes, and Spartan Weaponry melee weapons)
- **Max Level**: I
- **Description**: When held in the main hand:
  - `+10%` Attack Damage
  - `+10%` Attack Speed
  - `+10%` Movement Speed
  - `-10%` Max Health
  - `-10%` Armor

### 6. Heavyplated
- **Type**: Any armor piece (Helmet, Chestplate, Leggings, Boots)
- **Max Level**: I
- **Description**: Each equipped armor piece with Heavyplated grants:
  - `+5%` Armor
  - `-5%` Movement Speed
  - Stacks up to `+20%` Armor and `-20%` Movement Speed with a full set.

### 7. Autofire
- **Type**: Bows
- **Max Level**: II
- **Acquisition**: Super rare treasure (not available from Enchanting Table).
- **Description**: When firing a bow, trailing arrows travel immediately behind each arrow with identical velocity, draw power, and trajectory:
  - Level I: 2 arrows total (original + 1 trailing arrow).
  - Level II: 3 arrows total (original + 2 trailing arrows).
  - Removes target invincibility frames for all trailing arrows so every arrow deals full damage.
  - Fully compatible with Multishot / Splitshot from Rin's SoManyEnchantments.

### 8. Webber
- **Type**: Swords only
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Swords instantly break cobwebs on touch with zero delay, and hits have a `0.4%` chance to spawn a cobweb on the target.

### 9. Headhunter
- **Type**: Bows
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Incompatible With**: Mending, Advanced Mending (from SoManyEnchantments), and Infinity.
- **Description**: Arrows that hit an enemy's head / topmost hitbox area deal `1.5x` critical damage with magic crit particles and audio feedback.

### 10. Riches
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: II
- **Acquisition**: Available from Enchanting Table (Very Rare).
- **Description**: Killing a mob has a chance to trigger a 10x XP and 10x loot drop explosion:
  - Level I: 0.05% chance.
  - Level II: 0.1% chance.
  - Stacks additively with vanilla Looting and SoManyEnchantments Advanced Looting.

### 11. Slowsink
- **Type**: Boots
- **Max Level**: III
- **Description**: Grants feather-like slow falling when submerged in water, preventing rapid drowning or falling into oceanic hazards. Descent becomes progressively slower at higher levels. Sneaking allows sinking at normal speed.

### 12. Apex
- **Type**: Helmets
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Grants high-ground advantage:
  - Deals `+2%` damage per block of elevation above the target.
  - Takes `+2%` damage per block below attackers.

### 13. Royal Guard
- **Type**: Weapons & tools (except bows)
- **Max Level**: III
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: When taking damage, redirects `(10 * level)%` (10%, 20%, 30%) of durability damage that would be dealt to worn armor onto the durability of the held weapon or tool instead. Accompanied by a shield block sound effect.

### 14. Curse of the Tether
- **Type**: Chestplates
- **Max Level**: I (Curse)
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: You take `10%` less damage from all attacks and `20%` less durability damage to your chestplate, but the chestplate cannot be removed or dropped from your chest in Survival mode.

### 15. Curse of Inertia
- **Type**: Leggings
- **Max Level**: I (Curse)
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Continuous sprinting increases movement speed linearly up to `+80%` over 5 seconds. Turning radius while sprinting with momentum is severely restricted, and stopping incurs 1.5 seconds (30 ticks) of ice-like sliding deceleration across all block surfaces.

### 16. Aperture
- **Type**: Shields (Vanilla and Spartan Shields)
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Captures incoming enemy projectiles (Skeleton arrows, Ice and Fire dragon breath orbs, Lycanites Cacodemon balls, Ghast fireballs, etc.) upon contact with your shield. The projectile does not trigger hit logic; its entity instance is captured, re-aimed along your `player.getLookVec()` trajectory, given a `+50%` speed boost, and re-fired from eye height as an allied projectile.

### 17. Gyroscopic Plating
- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Absolute spatial stability and momentum conversion:
  - **Camera Stabilization**: Locks camera roll to 0.0° on the client, completely immunizing against screen spin, inversion, and forced camera tilt (Lycanites Instability, Nausea, Potion Core Spin & Perplexity).
  - **Momentum Slingshot**: If you fall from a height $> 4$ blocks and tap Crouch right before landing, 100% of vertical fall momentum is converted into horizontal forward velocity ($v_{\text{horizontal}} = \sqrt{2 \cdot g \cdot h}$ along your look vector), zeroing out all fall damage and slingshotting you forward.

### 18. Slip-Bore
- **Type**: Bows and Crossbows
- **Max Level**: I
- **Acquisition**: Enchanting Table (Very Rare, Level 30).
- **Description**: Arrows punch a low-pressure vacuum tunnel (3-block radius) through the air along their flight path. Nearby entities have their velocity yanked inward toward the trajectory centerline, dragging flying mobs (Cacodemons, Raikos, Phantoms) crashing down to the ground and pulling ground mobs into a tight line behind the arrow.

### 19. Harmonic Anchor
- **Type**: Fishing Rods
- **Max Level**: I
- **Acquisition**: Enchanting Table (Very Rare, Level 30).
- **Description**: Hooking a mob does not pull them to you; it drops a spectral anchor stone at the mob's feet. For the next 8 seconds (160 ticks), the mob cannot move more than 5 blocks away from the anchor coordinate. If they attempt to lunge, charge, or fly away, the anchor line pulls taut, slingshotting them backward to the center point.

### 20. Perpetual Crucible
- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Potion Core Integration**: Directly drives `potioncore:extension`.
- **Description**: Landing critical hits or killing a mob grants the player Extension for 3 seconds (60 ticks). In Potion Core, Extension pauses other potion durations from ticking down, completely freezing active combat buffs (Wine buffs, Battle Burrito buffs, Diamond Skin, Golden Apple Absorption) as long as you stay aggressively engaged in combat.

### 21. Gambler's Gambit
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: I
- **Acquisition**: Enchanting Table (Super Rare and Costly, Level 30).
- **Description**: Striking an enemy inflicts a random negative status effect selected from vanilla and Potion Core curses (Slowness, Weakness, Poison, Wither, Blindness, Levitation, Broken Armor, Vulnerable, Weight, Perplexity, Spin, Rust, Lightning).

### 22. Grounded Vanguard
- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Enchanting Table (Very Rare, Level 28).
- **Potion Core Integration**: Directly drives `potioncore:solid_core` and `potioncore:weight` III.
- **Description**: Whenever you sneak, the boots immediately grant Solid Core (cancels knockback) and Weight III (heavy downward gravity vector), preventing any entity (Rocs, Raikos, Djinns) from lifting, carrying, or displacing you. Releasing sneak smoothly purges the effect without fall damage.

### 23. Curse of the Volatile Soul
- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Treasure only (Expensive super rare, min level 30, not available from Enchanting Table).
- **Curse**: Yes.
- **Description**: Acts as an explosive Totem of Undying upon death. When the player takes fatal damage:
  - Consumes the chestplate completely.
  - Revives the player with 4 HP (2 hearts), clears negative potion effects, and grants Regeneration II (45s), Absorption II (5s), Fire Resistance (40s), and Resistance V (5s).
  - Triggers the official vanilla Totem of Undying visual animation (`byte 35`) and sound effects.
  - Detonates an apocalyptic **power 20 explosion** (`world.newExplosion`) that obliterates mobs and ruins surrounding terrain with zero damage or knockback dealt to the wearer.

### 24. Breach
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: IV
- **Acquisition**: Enchanting Table.
- **Potion Core Integration**: Applies `potioncore:vulnerable`.
- **Description**: Each strike has a `(20 * level)%` chance to inflict Potion Core Vulnerability on the target for 15 seconds (300 ticks), and a `(15 * level)%` chance to inflict 10x durability damage to the victim's worn armor pieces.

### 25. Demonic Edge
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: V
- **Acquisition**: Enchanting Table.
- **Incompatible With**: Sharpness, Smite, Bane of Arthropods.
- **Lycanites Mobs Integration**: Targets Demon and Shadow tagged creatures.
- **Description**: Deals `+2.5 * level` bonus damage against Nether mobs (Blazes, Ghasts, Magma Cubes, Wither Skeletons, Withers, Zombie Pigmen, Nether dimension fire-immune mobs) and Lycanites Mobs creatures with Demon type or Shadow element (Rahovart, Belph, Cacodemon, Behemoth, Amalgalich, etc.).

### 26. Necrotic Stasis
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: II
- **Acquisition**: Enchanting Table (5 levels per tier: min enchantability 5, 10).
- **Lycanites Mobs Integration**: Applies `lycanitesmobs:decay`.
- **Description**: Melee attacks inflict Decay for 10 seconds (200 ticks). At Level II, also completely purges and strips Regeneration and Absorption potion effects and bonus health hearts from the target.

### 27. Volatile Discharge
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: III
- **Acquisition**: Enchanting Table.
- **Description**: Each strike builds an electric volatile charge of `(3 * level)%` on the target. Upon reaching 100% charge or higher, the entity violently detonates in a creeper-style explosion.

### 28. Muzzle
- **Type**: Crossbows (Spartan Weaponry & modded crossbows)
- **Max Level**: I
- **Acquisition**: Enchanting Table (Super Rare Level 30 enchant).
- **Lycanites Mobs Integration**: Applies `lycanitesmobs:aphagia`.
- **Description**: Fired crossbow projectiles afflict targets with Aphagia for 10 seconds (200 ticks), disabling item usage and consumption (eating, potions, active items).

### 29. Archery
- **Type**: Bows and Crossbows
- **Max Level**: III
- **Acquisition**: Enchanting Table (Costs 8 levels per tier: min enchantability 8, 16, 24).
- **Potion Core Integration**: Directly drives `potioncore:archery` (Trueshot).
- **Description**: Bow and crossbow kills grant Trueshot I for 10 seconds.
  - Level I: Killing 1 enemy grants Trueshot I for 10 seconds.
  - Level II: Killing 4 enemies within those 10 seconds upgrades the buff to Trueshot II for 10 seconds.
  - Level III: Killing 10 enemies within those 10 seconds upgrades the buff to Trueshot III for 10 seconds.

### 30. Arachnid Ascent
- **Type**: Leggings
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Potion Core Integration**: Drives `potioncore:climb` (Climbing).
- **Description**: Wearing the leggings grants the Climbing status effect, allowing the player to walk directly up vertical walls and cliffs like a spider. Automatically removed when unequipped.

### 31. Umbral Prowess
- **Type**: Helmets
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: In deep darkness (light level 4 or below), grants the wearer innate Night Vision, +20% movement speed, and +20% physical melee damage with abyssal portal particles. However, stepping into bright light (light level 12 or above) inflicts a minor Slowness I debuff, rewarding subterranean and nocturnal hunters.

### 32. Gourmand’s Vitality
- **Type**: Chestplates
- **Max Level**: V
- **Acquisition**: Enchanting Table.
- **Incompatible With**: `somanyenchantments:strengthenedvitality`.
- **Description**: Eating any food item in combat triggers an emergency surge of survival buffs scaled by enchantment level:
  - Regeneration: `3 * level` seconds.
  - Resistance: `5 * level` seconds.
  - Absorption: `10 * level` seconds.
  - Heartier meals (hunger $\ge 7$ or saturation $\ge 0.8$) boost the potion amplifier to II.

### 33. Ballistic Overdrive
- **Type**: Crossbows
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Incompatible With**: Rapid Load, Strafe, and rapid-firing crossbow enchantments.
- **Description**: Loaded crossbow projectiles completely ignore gravity (`noGravity = true`), travelling in a laser-straight horizontal vector with infinite punch-through piercing through non-boss entities until colliding with solid terrain or a boss monster.

### 34. Curse of the Debt of the Void
- **Type**: All Equipment (Weapons, Tools, Armor, Shields, Bows, Crossbows)
- **Max Level**: I
- **Curse**: Yes.
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Prevents the cursed item from ever losing durability. In exchange, every point of durability damage the item would have suffered is converted directly into hunger depletion ($1.5\times$ exhaustion) and minor true void damage ($0.25\times$ unmitigated damage) dealt to the wearer.

### 35. Curse of Berserker’s Bloodlust
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: I
- **Curse**: Yes.
- **Acquisition**: Enchanting Table.
- **Description**: Base attack damage scales up to +150% higher as the wielder's health drops toward death. In exchange, incoming healing from natural regeneration, healing potions, and lifesteal is reduced by 75%.

### 36. Splintering Impact
- **Type**: Crossbows
- **Max Level**: III
- **Acquisition**: Enchanting Table.
- **Description**: When a crossbow arrow or bolt strikes a solid block instead of an entity, it violently shatters into homing grenade shrapnel shards:
  - Level I: 4 shrapnel arrows seeking hostile mobs within 6 blocks.
  - Level II: 9 shrapnel arrows seeking hostile mobs within 9 blocks.
  - Level III: 13 shrapnel arrows seeking hostile mobs within 12 blocks.

### 37. Headslicer
- **Type**: Melee weapons (Swords, Axes, Spartan Weaponry)
- **Max Level**: III
- **Acquisition**: Enchanting Table.
- **Description**: Airborne melee strikes deal bonus damage when timed at the crest/apex of a jump where vertical momentum is near zero (`|motionY| <= 0.12`):
  - Level I: +15% bonus damage.
  - Level II: +30% bonus damage.
  - Level III: +50% bonus damage.

### 38. Longdraw
- **Type**: Bows
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Incompatible With**: Mending, Strafe, Multishot, Splitshot, Rapid Fire, Range.
- **Description**: Allows bows to be drawn past 100% up to 250% overdraw power (requiring 50 ticks of full hold). Releasing at 250% triggers a concussive muzzle blast that pierces up to 5 targets with scaled damage and recoils the archer backward 6 blocks.

### 39. Suregrip
- **Type**: Weapons and Tools
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table).
- **Description**: Completely locks the held weapon or tool in the player's grasp, preventing disarms, drops, and item theft from Infernal Sticky affixes and creature disarm abilities. Due to the two-handed white-knuckle grip, holding any item in the offhand forces the offhand item to drop immediately.

### 40. Cushioned Heel
- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Dampens kinetic wall-impact collision damage (Elytra fly-into-wall, explosive knockback into terrain) and minor fall damage ($\le 4.5$ blocks) by 30%.

### 41. Mudstrider
- **Type**: Boots
- **Max Level**: III
- **Acquisition**: Enchanting Table.
- **Description**: Negates the movement speed penalties and drag of non-solid friction blocks (Soul Sand, Mud, Cobwebs, and shallow water) while keeping standard dry-land sprint speed at the vanilla baseline.

### 42. Culling
- **Type**: Axes and Daggers
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Deals +15% bonus damage exclusively against weakened targets whose current health is already below 20% of their maximum health.

### 43. Dulled Impact
- **Type**: Blunt weapons, Warhammers, Hammers, Clubs, Maces, and Flails
- **Max Level**: III
- **Acquisition**: Enchanting Table.
- **Description**: Heavy blunt strikes daze the target, reducing their outgoing attack damage by a flat 1 (Level I), 2 (Level II), or 3 (Level III) points (0.5, 1, or 1.5 hearts) for 2 seconds (40 ticks). Does not stack.

### 44. Glimmer
- **Type**: Bows and Crossbows
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Fired arrows and bolts leave a shimmering trail of particles and mark struck targets with the Glowing status effect for 5 seconds without inflicting bonus damage.

### 45. Braced Pivot
- **Type**: Shields
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Reinforces shield pivot balance, shortening the shield-disable stun timer from heavy axe strikes and smashes by 1 second (cooldown reduced by 20 ticks).

### 46. Feathered Draw
- **Type**: Bows
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Reduces the movement speed penalty while drawing a bow from vanilla's -80% down to -50%, allowing agile repositioning and strafing while aiming without affecting draw speed, velocity, or arrow damage.

### 47. Stagger
- **Type**: Maces and Axes (Blunt weapons & Axes)
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Melee strikes briefly pause a basic mob's attack windup animation by 0.25 seconds (5 ticks), interrupting an immediate counter-swing. Bosses and champions are immune. Does not stun or freeze enemies.

### 48. Puncture
- **Type**: Piercing weapons and Spears (Spartan Weaponry daggers, spears, pikes, lances, rapiers)
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Piercing melee strikes bypass a flat 2 points of natural mob armor (e.g., treating a 10-armor mob as having 8 armor). Flat reduction provides effective chip damage against early armored foes without trivializing end-game boss armor.

### 49. Tempered Core
- **Type**: Weapons and Tools (Swords, Axes, Pickaxes, Shovels, Hoes)
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Eliminates the extra durability penalty for using the wrong tool on a block or mob (e.g., hitting a zombie with a pickaxe or slicing cobwebs with a sword only consumes 1 durability instead of 2).

### 50. Scavenger's Habit
- **Type**: Bows and Thrown Weapons
- **Max Level**: IV
- **Acquisition**: Enchanting Table.
- **Description**: Killing an enemy with an arrow or thrown weapon has a `(25 * level)%` chance (up to 100% at Level IV) to refund the projectile directly into the player's inventory. Applies only to the initial projectile. Mutually exclusive with `somanyenchantments:arrowrecovery`.

### 51. Thermal Foil
- **Type**: Any armor piece (Helmet, Chestplate, Leggings, Boots)
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Slows body temperature swings and climate change rate in **Tough As Nails** or **Simple Difficulty** by 20% per piece worn (up to 80% reduction with a full set), stabilizing body heat in extreme environments.

### 52. Machine Bow
- **Type**: Bows
- **Max Level**: IV
- **Acquisition**: Enchanting Table.
- **Description**: Reaching 100% draw power fires the arrow automatically and immediately resets draw for continuous rapid-fire bursts. Higher levels increase draw speed and decrease recoil pitch/yaw pushback and arrow spread. Mutually exclusive with Multishot, Rapid Fire, Strafe, and Infinity.

### 53. Desalination Mesh
- **Type**: Helmets
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Reduces infection chance from drinking dirty or salt water (such as Parasites in Tough As Nails or Simple Difficulty) by 85% when drinking directly from a water source with an empty hand.

### 54. Metabolic Slumber
- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Pauses hunger and thirst depletion while the player remains completely stationary and idle. Takes effect after 10 seconds (200 ticks) of resting.

### 55. Sober Chisel
- **Type**: Pickaxes
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Carefully chisels ores to reduce the chance of triggering aggressive ore-spawn entities (like Lycanites Mobs Geonachs from coal/iron or Tremors from stone) by 75%, at the cost of a minor -5% mining speed penalty.

### 56. Shimmered
- **Type**: Pickaxes
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Breaking an ore block highlights directly adjacent identical ore blocks within a 2-block radius with a brief 1-second particle shimmer, pinpointing hidden adjacent veins.

### 57. Silt Sifter
- **Type**: Shovels
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Digging natural gravel, clay, sand, or dirt has a 5% chance to sift out minor foraging bits (flint, stray string, clay balls, or iron nuggets).

### 58. Dampening Strike
- **Type**: Pickaxes and Hammers
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Mining blocks produces 70% less vibration and acoustic sound radius, preventing subterranean predators from aggroing through cave walls.

### 59. Cushioned Shirt
- **Type**: Chestplates
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Dampens horizontal kinetic impact damage by 60% when colliding with terrain while flying (Elytra), grappling, or riding at high speed.

### 60. Magnetic Clasp
- **Type**: Boots and Chestplates
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Attracts dropped items within 3 blocks (6 blocks if worn on both boots and chestplate), but only if that exact item already exists in your inventory in an incomplete stack, avoiding inventory clutter.

### 61. Ground Sweep
- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Super rare treasure only (not available from Enchanting Table).
- **Description**: Restores vanilla automatic walk-over collision item pickup, collecting items you walk over without requiring manual right-clicking or aiming (countering ItemPhysic Full restrictions).

### 62. Shadow Stalker
- **Type**: Leggings
- **Max Level**: II
- **Acquisition**: Enchanting Table.
- **Description**: Reduces the detection radius of unaware, sleeping, or idle mobs by 25% (Level I) or 50% (Level II) while sneaking, preventing accidental aggro from multi-level dungeon rooms or floor grates. Breaks immediately if the player sprints, attacks, or steps into direct line-of-sight within 6 blocks.

### 63. Challenger's Mark
- **Type**: Shields
- **Max Level**: II
- **Acquisition**: Enchanting Table.
- **Description**: Performing a shield bash on an enemy forces that target to lock onto the player for 3 seconds (Level I) or 6 seconds (Level II), breaking its target lock on passive pets, tamed mounts, or party members. Has a 6-second cooldown per target and provides no damage buffs; purely an aggro-redirection utility.

### 64. Low Sweep
- **Type**: Polearms and Halberds (Spartan Weaponry Halberds, Pikes, Spears, Glaives, Lances)
- **Max Level**: III
- **Acquisition**: Enchanting Table.
- **Description**: Sweeping strikes prioritize ankle-height hitboxes, applying a 0.5-second stagger to ground-crawling or short mobs (Silverfish, small spiders, hounds) and spacing them back. The sweep damage itself is reduced by 30%, functioning purely as a crowd-control spacing tool.

### 65. Gargoyle Stance
- **Type**: Boots
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Eliminates involuntary displacement from external suction and vacuum effects (such as Lycanites Djinn vortexes, underwater whirlpools, or siren pulls) as long as both feet remain firmly on solid blocks. Does not protect against standard forward mob knockback or vertical explosive blasts.

### 66. Quiet Chipping
- **Type**: Pickaxes and Hammers
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Reduces the acoustic vibration range of mining blocks by 75%, significantly lowering the chance of alerting nearby sound-tracking subterranean predators through cave walls.

### 67. Salvage Line
- **Type**: Fishing Rods
- **Max Level**: I
- **Acquisition**: Enchanting Table.
- **Description**: Catching non-fish junk items (leather boots, rusted tools, string, bones) automatically breaks them down into bonus experience points and useful repair materials (nuggets, leather, string, bone meal).

### 68. Rune: Carnage
- **Type**: Nunchaku, Sabers, and Rapiers (Spartan Weaponry, Better Survival, and modded equivalents)
- **Max Level**: III
- **Acquisition**: Super rare Enchanting Table enchantment (Level 30). Costs 8 levels per tier (8/16/24).
- **Incompatible With**: `somanyenchantments:swifterslashes` (Swifter Slashes).
- **Description**: Permanently reduces attack speed by `10 * level%`. Every fully charged hit (attack indicator completely full) instead grants `+2%` / `+3%` / `+5%` attack speed per stack, stacking up to `7 * level` times, and shaves `level%` off the victim's invulnerability frames. Going 5 seconds without hitting an enemy removes 5 stacks, repeating until the stacks reach zero. The enchantment name is displayed in green.

### 69. Enlargement
- **Type**: All melee weapons
- **Max Level**: II
- **Acquisition**: Enchanting Table.
- **Description**: Melee hits have a `2 * level%` chance to apply the custom **Enlargement** potion effect for 20 seconds, increasing the target's model size and hitbox by `10 * level%`. The effect uses a custom pixel-art status icon and is synchronized to nearby clients.

### 70. Bullseye
- **Type**: Bows
- **Max Level**: IV
- **Acquisition**: Enchanting Table.
- **Description**: Hitting an enemy with an arrow marks a small bullseye on them for 3 seconds. Arrows whose flight path passes through the bullseye deal an additional `20 * level%` damage. Any further bow hit refreshes and repositions the bullseye. The bullseye is rendered as a camera-facing pixel-art target attached to the enemy.

### 71. Hunter
- **Type**: Bows
- **Max Level**: III
- **Acquisition**: Enchanting Table.
- **Description**: Arrows apply Glowing to the struck enemy for `20 * level * draw` seconds, where `draw` is the bow's charge at release (`0.0` to `1.0`). A fully drawn Level III bow marks targets for 60 seconds.

### 72. Voodoo
- **Type**: All melee weapons
- **Max Level**: VI
- **Acquisition**: Treasure only (not available from Enchanting Table). Red enchantment name.
- **Description**: Slaying an enemy with the weapon has a `2 * level%` chance to raise a loyal zombie ally from the ground at the corpse:
  - Health: `4 * level`.
  - Movement speed: 35% faster than a normal zombie.
  - Attack damage: `3 + level`.
  - Follows its owner across dimensions and teleports when left far behind.
  - Attacks hostile mobs and anything that attacks its owner.
  - Cannot pick up items or armor, cannot hurt or be hurt by its owner, and has a red glowing outline.
  - Displayed name: `[Player]'s Ally`.
  - Spawn sound: Zombie Villager curing sound.

### 73. Ancient: Kratos Blades
- **Type**: Sabers only
- **Max Level**: I
- **Acquisition**: Treasure only (not available from Enchanting Table). Yellow enchantment name.
- **Description**: While the original blade is held, the other hand immediately drops whatever it is holding and receives a functional duplicate of the blade. Right-clicking hurls a fishing-rod-style chain; the first mob or player it touches is violently pulled toward the wielder, consuming exactly 1 durability from the original blade. The duplicate vanishes when the original is no longer held and cannot be dropped.

---

## Mod Compatibility & Integrations
- **JEI (Just Enough Items)**: Automatically registers description pages for all 73 enchanted books via `@JEIPlugin`.
- **Enchantment Descriptions Mod**: Full tooltip integration via sneak inspection (`enchantment.<modid>.<name>.desc`). Colored names (`Rune: Carnage` green, `Voodoo` red, `Ancient: Kratos Blades` yellow) are preserved.
- **Potion Core**: Deep integration with `potioncore:extension` (Perpetual Crucible), `potioncore:solid_core` and `potioncore:weight` (Grounded Vanguard), `potioncore:vulnerable` (Breach), `potioncore:climb` (Arachnid Ascent), `potioncore:archery` (Archery), and the curse pool (Gambler's Gambit).
- **Spartan Weaponry & Spartan Shields**: Full weapon, shield, and crossbow classification support across all custom items (Daggers, Hammers, Warhammers, Clubs, Heavy Shields, Crossbows).
- **Lycanites Mobs**: Ephemeral Aegis summoning (`Ally`), projectile reflection (`Aperture`), anti-lift grounding (`Grounded Vanguard`), instability immunity (`Gyroscopic Plating`), Aphagia silencing (`Muzzle`), Decay infliction (`Necrotic Stasis`), Demon/Shadow creature detection (`Demonic Edge`), ore-spawn entity suppression (`Sober Chisel`), and Boss entity classification.
- **Rin's SoManyEnchantments**: Full synergy with Multishot / Splitshot trailing arrow spawning, Advanced Mending mutual exclusion (`Headhunter`), Advanced Looting drop amplification (`Riches`), Strengthened Vitality mutual exclusion (`Gourmand's Vitality`), and Arrow Recovery mutual exclusion (`Scavenger's Habit`).
- **Tough As Nails & Simple Difficulty**: Body temperature swing stabilization (`Thermal Foil`), parasite infection reduction from untreated water sources (`Desalination Mesh`), and hunger/thirst pause during stationary rest (`Metabolic Slumber`).
- **ItemPhysic Full**: Automatic walk-over collision pickup restoration (`Ground Sweep`).

## Building & Installation
Run `./gradlew build` in the `DivineEnchantments` directory. The built mod jar will be in `build/libs/DivineEnchantments-1.12.2-1.0.0.jar`.
