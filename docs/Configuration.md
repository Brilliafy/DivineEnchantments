# Configuration

Divine Enchantments creates its config at:

```text
config/divineenchantments.cfg
```

Config changes require a **game restart**. The server is authoritative for gameplay values; keep client and server configs aligned for consistent tooltips and enchanting-table behavior.

## General

| Key | Default | Description |
| :-- | :-- | :-- |
| `enableAllEnchantments` | `true` | Master switch. When false, every enchantment is inert and cannot be applied. |

Table level ranges are fixed in code. See [Enchanting Table Levels](EnchantingTable.md) for every table enchantment, its first level, and its tier unlock levels.

## Enchantment toggles

Every enchantment has a boolean entry under the `enchantments` category:

```cfg
enchantments {
    B:chainsaw=true
    B:rune_carnage=true
    B:voodoo=true
    B:kratos_blades=true
}
```

- Disabled enchantments cannot be applied at the enchanting table or anvil.
- Their effects stop working on existing items.
- Disabled enchantments are hidden from JEI.
- Disabled enchantments are stripped from existing items on the server: inventories, open containers, item entities, item frames, mob equipment, drops, and villager trade offers.
- An enchanted book with no remaining stored enchantments becomes a vanilla book.
- The `ally` toggle is always present; it only has an effect when Lycanites Mobs is installed.

## Compatibility

| Key | Default | Description |
| :-- | :-- | :-- |
| `fixRangeEnchantmentDamage` | `true` | Removes the extra arrow damage Better Survival's Range enchantment gains from its velocity boost. Arrows still fly faster and further. |

## Balance settings

These live under the `balance` category.

### Rune: Carnage

| Key | Default | Description |
| :-- | :-- | :-- |
| `carnagePenaltyPerLevel` | `0.0` | Attack speed penalty per level. The default is no penalty. |
| `carnageBaseBonusPerLevel` | `0.055` | Immediate multiplicative attack speed per enchantment level (5.5%). |
| `carnageBonusLevel1` | `0.01` | Multiplicative attack speed per stack at level I (1%). |
| `carnageBonusLevel2` | `0.01` | Multiplicative attack speed per stack at level II. |
| `carnageBonusLevel3` | `0.01` | Multiplicative attack speed per stack at level III (about 1.375x at 21 stacks, same as Swifter Slashes III on a sword). |
| `carnageMaxStacksPerLevel` | `7` | Maximum stacks per level (7 = 7/14/21). |
| `carnageDecayTicks` | `100` | Ticks without a hit before stacks decay. |
| `carnageDecayStacks` | `5` | Stacks lost per decay interval. |

### Enlargement

| Key | Default | Description |
| :-- | :-- | :-- |
| `enlargementChancePerLevel` | `0.02` | Proc chance per level. |
| `enlargementSizePerLevel` | `0.10` | Size increase per level. |
| `enlargementMaxSizeMultiplier` | `1.30` | Maximum size multiplier (30% larger). |
| `enlargementDurationTicks` | `400` | Effect duration in ticks. |

### Feathered Draw

| Key | Default | Description |
| :-- | :-- | :-- |
| `featheredDrawSpeedLevel1` | `0.8` | Removes 20% of draw slowdown (-64% remaining). |
| `featheredDrawSpeedLevel2` | `1.6` | Removes 40% of draw slowdown (-48% remaining). |
| `featheredDrawSpeedLevel3` | `2.4` | Removes 60% of draw slowdown (-32% remaining). |
| `featheredDrawSpeedLevel4` | `3.2` | Removes 80% of draw slowdown (-16% remaining). |

### Splintering Impact / Autofire

| Key | Default | Description |
| :-- | :-- | :-- |
| `splinteringDamageMultiplier` | `0.50` | Shard damage as a multiple of the original bolt's damage. Power/Advanced Power scales it. |
| `autofireDamageMultiplier` | `0.60` | Damage per Autofire arrow, including the original. |

### Dulled Impact / Puncture

| Key | Default | Description |
| :-- | :-- | :-- |
| `dulledImpactReductionPerLevel` | `0.10` | Outgoing damage reduction per level. |
| `dulledImpactDurationTicks` | `100` | Dulled Impact duration in ticks (100 = 5 seconds). |
| `punctureArmorBypass` | `0.10` | Fraction of natural armor bypassed. |

### Bullseye

| Key | Default | Description |
| :-- | :-- | :-- |
| `bullseyeDamagePerLevel` | `0.20` | Bonus damage per level. |
| `bullseyeDurationTicks` | `60` | Marker lifetime in ticks. |
| `bullseyeHitRadius` | `0.30` | Bullseye hit radius in blocks. |

### Braced Pivot / Volatile Discharge

| Key | Default | Description |
| :-- | :-- | :-- |
| `bracedPivotCooldownReduction` | `0.60` | Fraction of the base 100-tick shield cooldown removed. |
| `volatileDischargeExplosionPower` | `5.0` | Explosion power when Volatile Discharge reaches 100%. |

### Stagger

| Key | Default | Description |
| :-- | :-- | :-- |
| `staggerParalysisTicks` | `60` | Lycanites Paralysis duration from Stagger (3 seconds). |

### Hunter

| Key | Default | Description |
| :-- | :-- | :-- |
| `hunterDurationPerLevelTicks` | `400` | Glowing duration per level at full draw. |

### Apex

| Key | Default | Description |
| :-- | :-- | :-- |
| `apexMaxBonus` | `0.25` | Maximum high-ground damage bonus and falling penalty. |

### Gambler's Gambit

| Key | Default | Description |
| :-- | :-- | :-- |
| `gamblersGambitChance` | `0.25` | Chance per hit to apply a negative effect. |
| `gamblersGambitBlacklist` | empty | Potion registry names or paths to exclude. Example: `potioncore:flight`. |

### Perpetual Crucible

| Key | Default | Description |
| :-- | :-- | :-- |
| `perpetualCrucibleBaseDurationTicks` | `60` | Extension duration at level I in ticks. |
| `perpetualCrucibleDurationPerLevelTicks` | `30` | Extra Extension duration per level in ticks. |

### Shimmered

| Key | Default | Description |
| :-- | :-- | :-- |
| `shimmeredHighlightDurationTicks` | `20` | Ore outline duration in ticks (20 = 1 second). |
| `shimmeredHighlightRadius` | `2` | Radius scanned for identical ore blocks. |

### Sound stealth

| Key | Default | Description |
| :-- | :-- | :-- |
| `dampeningStrikeAggroRadius` | `4.8` | Maximum mob targeting distance while holding a Dampening Strike tool. |
| `quietChippingAggroRadius` | `4.0` | Maximum mob targeting distance while holding a Quiet Chipping tool. |

### Ally

| Key | Default | Description |
| :-- | :-- | :-- |
| `allyMaxAegisPerPlayer` | `4` | Maximum living Aegis per player across all Ally weapons. |

### Voodoo

| Key | Default | Description |
| :-- | :-- | :-- |
| `voodooChancePerLevel` | `0.02` | Ally spawn chance per level. |
| `voodooHealthPerLevel` | `4.0` | Ally health per level. |
| `voodooSpeedMultiplier` | `1.35` | Ally speed multiplier over a normal zombie. |
| `voodooBaseDamage` | `3.0` | Ally base attack damage. |
| `voodooDamagePerLevel` | `1.0` | Ally bonus damage per level. |
| `voodooMaxAlliesPerPlayer` | `6` | Maximum living allies per player (capped at 6). Set to `0` to disable spawning. |

### Ancient: Kratos Blades

| Key | Default | Description |
| :-- | :-- | :-- |
| `kratosDurabilityCost` | `1` | Durability consumed by the chain on hit. |
| `kratosHookSpeed` | `2.1` | Chain launch speed. |
| `kratosPullPower` | `1.35` | Chain pull strength. |
| `kratosHookLifetimeTicks` | `70` | Chain lifetime in ticks. |
| `kratosCooldownTicks` | `8` | Chain cooldown in ticks. |

## Multiplayer notes

- **The server is authoritative.** On login and respawn, the server sends its config snapshot to the client and overwrites the client's runtime values.
- Enlargement scale factors, Rune: Carnage attack-speed values, and all other gameplay balance are server-controlled.
- Disabled enchantments are stripped from existing items on the server: player inventories, open containers, item entities, item frames, mob equipment, drops, and villager trade offers.
- Enchanted books that lose every stored enchantment are converted to vanilla books.
- JEI may need a game restart to reflect a server's different per-enchantment toggles.
