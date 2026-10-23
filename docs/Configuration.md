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
- When Lycanites Mobs is installed, `ally` receives a toggle as well.

## Balance settings

These live under the `balance` category.

### Rune: Carnage

| Key | Default | Description |
| :-- | :-- | :-- |
| `carnagePenaltyPerLevel` | `0.10` | Attack speed penalty per level. |
| `carnageBonusLevel1` | `0.02` | Attack speed per stack at level I. |
| `carnageBonusLevel2` | `0.03` | Attack speed per stack at level II. |
| `carnageBonusLevel3` | `0.05` | Attack speed per stack at level III. |
| `carnageMaxStacksPerLevel` | `7` | Maximum stacks per level (7 = 7/14/21). |
| `carnageDecayTicks` | `100` | Ticks without a hit before stacks decay. |
| `carnageDecayStacks` | `5` | Stacks lost per decay interval. |
| `carnageVulnerabilityPerLevel` | `0.01` | Fraction of enemy invulnerability frames removed per level. |

### Enlargement

| Key | Default | Description |
| :-- | :-- | :-- |
| `enlargementChancePerLevel` | `0.02` | Proc chance per level. |
| `enlargementSizePerLevel` | `0.10` | Size increase per level. |
| `enlargementDurationTicks` | `400` | Effect duration in ticks. |

### Bullseye

| Key | Default | Description |
| :-- | :-- | :-- |
| `bullseyeDamagePerLevel` | `0.20` | Bonus damage per level. |
| `bullseyeDurationTicks` | `60` | Marker lifetime in ticks. |
| `bullseyeHitRadius` | `0.30` | Bullseye hit radius in blocks. |

### Hunter

| Key | Default | Description |
| :-- | :-- | :-- |
| `hunterDurationPerLevelTicks` | `400` | Glowing duration per level at full draw. |

### Voodoo

| Key | Default | Description |
| :-- | :-- | :-- |
| `voodooChancePerLevel` | `0.02` | Ally spawn chance per level. |
| `voodooHealthPerLevel` | `4.0` | Ally health per level. |
| `voodooSpeedMultiplier` | `1.35` | Ally speed multiplier over a normal zombie. |
| `voodooBaseDamage` | `3.0` | Ally base attack damage. |
| `voodooDamagePerLevel` | `1.0` | Ally bonus damage per level. |
| `voodooMaxAlliesPerPlayer` | `8` | Maximum living allies per player. Set to `0` to disable spawning. |

### Ancient: Kratos Blades

| Key | Default | Description |
| :-- | :-- | :-- |
| `kratosDurabilityCost` | `1` | Durability consumed by the chain on hit. |
| `kratosHookSpeed` | `2.1` | Chain launch speed. |
| `kratosPullPower` | `1.35` | Chain pull strength. |
| `kratosHookLifetimeTicks` | `70` | Chain lifetime in ticks. |
| `kratosCooldownTicks` | `8` | Chain cooldown in ticks. |

## Multiplayer notes

- Gameplay balance is applied by the server.
- Enlargement scale factors and Rune: Carnage attack speed values are synchronized from server to client.
- Client configs are used for JEI, tooltips, and local enchanting-table checks. Keep them aligned with the server to avoid display differences.
