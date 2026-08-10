# Colorful Armor Bar

Tired of Netherite, Diamond, and Leather looking exactly the same on your HUD? **Colorful Armor Bar** is a lightweight, client-side mod that colors your armor bar icons based on the exact gear you're wearing. 

## How it Works
The project is heavily inspired by [*Detail Armor Bar*](https://modrinth.com/mod/detail-armor-bar), but under the hood, this mod takes a completely data-driven approach. 

Instead of hardcoding compatibility or waiting for backend code updates, everything is driven by resource packs. Out of the box, the mod dynamically scans for textures mapped to your armor materials. If a mod adds a new armor tier, anyone can easily add flawless visual support for it with a resource pack—no code changes required.

## Features
* **Mix-and-Match (Split Rendering):** If you're wearing a diamond chestplate with netherite boots, the mod automatically splits your armor shield icons to accurately show both materials at once.
* **100% Client-Side:** Safe to use on any public server or multiplayer network. You don't need it installed on the server for it to work.
* **Fully Customizable:** Don't like the default colors? You can easily change the icons or add support for modded armor using resource packs.

## Configuration

The client config is stored in `config/colorful_armor_bar.json5`.

Set `group_matching_armor` to `true` to combine armor pieces that use the same
icon into one continuous section. The group with the most armor points is shown
first. Equal groups keep their worn-slot order. The default value is `false`,
which keeps the armor bar in worn-slot order.

---

## For Resource Pack Creators & Mod Developers
Supporting a custom armor set is incredibly straightforward. All you need to do is drop a `.png` texture into the folder matching the armor's material name.

The mod automatically checks this path structure:
`assets/<mod_id_or_namespace>/textures/armoricon/<material_name>.png`

* **Vanilla Example:** `assets/minecraft/textures/armoricon/diamond.png`
* **Modded Example:** If a mod named `flingleblob` adds a `ruby` armor set, you would place the texture in: `assets/flingleblob/textures/armoricon/ruby.png`

Armor-icon textures must be 18 pixels wide by 9 pixels high. The left and right
9-pixel halves are used for full and half-point rendering.

> **Note:** If the mod can't find a custom texture for an armor material, it safely falls back to a clean, default iron-style template.

## Supported build targets

The Stonecutter workspace currently produces these client artifacts:

* Minecraft 1.20.1: Fabric and Forge
* Minecraft 1.21.1: Fabric and NeoForge
* Minecraft 1.21.11: Fabric and NeoForge
* Minecraft 26.2: Fabric and NeoForge

Run `./gradlew build` to compile the active Stonecutter target, or address a
specific target directly, for example `./gradlew :1.21.1-neoforge:build`.
