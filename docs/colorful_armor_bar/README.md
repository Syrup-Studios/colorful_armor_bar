# Colorful Armor Bar

**Colorful Armor Bar** is a client-side mod that colors armor bar icons based on the armor you wear.

## How it Works
The visual idea is inspired by *Detail Armor Bar*. The mod uses resource packs to provide armor textures.

The mod scans resource packs for textures that match armor materials. A resource pack can add textures for armor from other mods without code changes.

## Features
* **Mix-and-Match (Split Rendering):** The mod splits armor icons to show different armor materials in the same icon.
* **Client-Side:** The mod does not need to be installed on the server.
* **Resource Pack Support:** Resource packs can change icons and add textures for modded armor.

---

## For Resource Pack Creators & Mod Developers
To add support for an armor set, add a `.png` texture to the folder for its armor material.

The mod automatically checks this path structure:
`assets/<mod_id_or_namespace>/textures/armoricon/<material_name>.png`

* **Vanilla Example:** `assets/minecraft/textures/armoricon/diamond.png`
* **Modded Example:** If a mod named `flingleblob` adds a `ruby` armor set, you would place the texture in: `assets/flingleblob/textures/armoricon/ruby.png`

> **Note:** If the mod cannot find a texture for an armor material, it uses the default iron-style texture.
