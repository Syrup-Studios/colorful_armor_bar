package net.syrupstudios.colorfularmorbar;

import net.syrupstudios.syruplibrary.config.ConfigSpec;
import net.syrupstudios.syruplibrary.config.SyrupConfigManager;
import net.syrupstudios.syruplibrary.config.value.ConfigValue;

public final class ColorfulArmorBarConfig {
    private static final ConfigSpec SPEC = ConfigSpec.builder(ColorfulArmorBar.MOD_ID)
            .header("Colorful Armor Bar client configuration")
            .build();

    public static final ConfigValue<Boolean> GROUP_MATCHING_ARMOR = SPEC.bool(
            "group_matching_armor",
            false,
            "Combine armor with the same icon, glint state, and trim appearance; show the largest group first.");
    public static final ConfigValue<Boolean> ARMOR_ICON_ANIMATION = SPEC.bool(
            "armor_icon_animation", true, "Animate armor icon changes with a wipe.");
    public static final ConfigValue<Boolean> DAMAGE_PULSE = SPEC.bool(
            "damage_pulse", true, "Show a red pulse when armor takes damage.");
    public static final ConfigValue<Boolean> REPAIR_PULSE = SPEC.bool(
            "repair_pulse", true, "Show a cyan pulse when armor is repaired.");
    public static final ConfigValue<Boolean> TRIM_GLOW = SPEC.bool(
            "trim_glow", true, "Show a pulsing highlight on amethyst, diamond, and emerald trims.");

    private ColorfulArmorBarConfig() {
    }

    public static void register() {
        SyrupConfigManager.getInstance().register(SPEC);
    }
}
