# Colorful Armor Bar: Configuration and Resource Packs

Colorful Armor Bar requires a matching Syrup Library version, which must be
installed separately.

## How it works

The visual idea is inspired by [*Detail Armor Bar*](https://modrinth.com/mod/detail-armor-bar). The mod uses resource packs to provide armor textures. It scans resource packs for textures that match armor materials. A resource pack can add textures for armor from other mods without code changes.

## Configuration

The client config is stored in `config/colorful_armor_bar.json5`.

Set `group_matching_armor` to `true` to combine armor pieces with the same
icon, enchantment glint state, and trim appearance into one continuous
section. The group with the most armor points is shown first. Equal groups keep
their worn-slot order. The default value is `false`, which keeps the armor bar
in worn-slot order.

The following effects are enabled by default:

| Key | Default | Effect |
| --- | --- | --- |
| `armor_icon_animation` | `true` | Wipe animation when armor icons change. |
| `damage_pulse` | `true` | Red pulse when armor takes damage. |
| `repair_pulse` | `true` | Cyan pulse when armor is repaired, including by Mending. |
| `trim_glow` | `true` | Pulsing highlight on amethyst, diamond, and emerald trims. |

Leather armor always uses its dye color. Trim material color also always
appears, even when `trim_glow` is disabled.

Use Minecraft's **Accessibility** settings to control enchantment glint speed
and strength. A glint speed of `0%` stops the armor-bar glint movement. A glint
strength of `0%` hides the glint.

You can also open the mod config in-game. On Fabric, use Mod Menu; on Forge or
NeoForge, open the Mods screen. Changes saved in the screen apply immediately.

## Resource pack guide

To add support for an armor set, add a `.png` texture to the folder for its armor material.

The mod checks this path:

`assets/<mod_id_or_namespace>/textures/armoricon/<material_name>.png`

- Vanilla example: `assets/minecraft/textures/armoricon/diamond.png`
- Modded example: `assets/flingleblob/textures/armoricon/ruby.png`

Armor-icon textures must be 18 pixels wide by 9 pixels high. The left and right
9-pixel halves are used for full and half-point rendering.
The leather icon is tinted with the armor's dye color. Use a white or grayscale
leather texture to keep dye colors accurate.

Each trim pattern can provide a texture at:

`assets/<namespace>/textures/armoricon/trims/<pattern>.png`

For example, the Sentry and Dune trim textures use these paths:

- `assets/minecraft/textures/armoricon/trims/sentry.png`
- `assets/minecraft/textures/armoricon/trims/dune.png`

If a pattern-specific texture is missing, the mod checks
`assets/colorful_armor_bar/textures/armoricon/trim.png`. It then falls back to
Minecraft's `textures/trims/items/chestplate_trim.png`. Trim textures can use
any size. Their alpha channel defines the mark, and the mod applies the trim
material color at run time.
Amethyst, diamond, and emerald trims also receive a subtle pulsing white
highlight inside the existing trim and armor masks.

If the mod cannot find a texture for an armor material, it uses the default
iron-style texture.
