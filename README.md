# Colorful Armor Bar

**Colorful Armor Bar** is a client-side mod that colors armor bar icons based on the armor you wear.

## How it Works
The project is inspired by [*Detail Armor Bar*](https://modrinth.com/mod/detail-armor-bar). It uses resource packs to provide armor textures.

The mod scans resource packs for textures that match armor materials. A resource pack can add textures for armor from other mods without code changes.

## Features
* **Mix-and-Match (Split Rendering):** The mod splits armor icons to show different armor materials in the same icon.
* **Armor Trims:** Trimmed armor adds a small trim mark to its armor points. The mark uses the trim material's color, including colors supplied by other mods.
* **Client-Side:** The mod does not need to be installed on the server.
* **Resource Pack Support:** Resource packs can change icons and add textures for modded armor.

Install the matching **Syrup Library** version separately. It is required by
Colorful Armor Bar and is no longer bundled with the mod.

## Configuration

The client config is stored in `config/colorful_armor_bar.json5`.

Set `group_matching_armor` to `true` to combine armor pieces with the same
icon, enchantment glint state, and trim appearance into one continuous
section. The group with the most armor points is shown first. Equal groups keep
their worn-slot order. The default value is `false`, which keeps the armor bar
in worn-slot order.

Set `glint_opacity` from `0` to `100` to control the opacity of the enchantment
glint. The default value is `30`. Set it to `0` to disable the glint.

You can also open these settings in-game from the mod's config screen. On
Fabric, use Mod Menu; on Forge or NeoForge, open the Mods screen. Changes
saved in the screen apply immediately.

---

## For Resource Pack Creators & Mod Developers
To add support for an armor set, add a `.png` texture to the folder for its armor material.

The mod automatically checks this path structure:
`assets/<mod_id_or_namespace>/textures/armoricon/<material_name>.png`

* **Vanilla Example:** `assets/minecraft/textures/armoricon/diamond.png`
* **Modded Example:** If a mod named `flingleblob` adds a `ruby` armor set, you would place the texture in: `assets/flingleblob/textures/armoricon/ruby.png`

Armor-icon textures must be 18 pixels wide by 9 pixels high. The left and right
9-pixel halves are used for full and half-point rendering.

Each trim pattern can provide its own armor-bar texture at
`assets/<namespace>/textures/armoricon/trims/<pattern>.png`. For example, the
Sentry and Dune trim textures use these paths:

* `assets/minecraft/textures/armoricon/trims/sentry.png`
* `assets/minecraft/textures/armoricon/trims/dune.png`

If a pattern-specific texture is missing, the mod checks
`assets/colorful_armor_bar/textures/armoricon/trim.png`. It then falls back to
Minecraft's `textures/trims/items/chestplate_trim.png`. Trim textures can use any
size. Their alpha channel defines the mark, and the mod applies the trim material
color at run time.

> **Note:** If the mod cannot find a texture for an armor material, it uses the default iron-style texture.

## Supported build targets

The Stonecutter workspace currently produces these client artifacts:

* Minecraft 1.20.1: Fabric and Forge
* Minecraft 1.21.1: Fabric and NeoForge
* Minecraft 1.21.11: Fabric and NeoForge
* Minecraft 26.2: Fabric and NeoForge
* Minecraft 26.3: Fabric and NeoForge

Run `./gradlew build` to compile all registered targets, or address a specific
target directly, for example `./gradlew :1.21.1-neoforge:build`.

To change the shared Stonecutter source target for IDE work, run for example
`./gradlew "Set active project to 1.21.1-fabric"`.

The project selects Java 21 for the Gradle daemon. The Minecraft 1.20.1
target still uses Java 17.
