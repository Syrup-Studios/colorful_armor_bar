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

    public static final ConfigValue<Integer> GLINT_OPACITY = SPEC.integer(
            "glint_opacity",
            30,
            0,
            100,
            "Opacity of the enchantment glint as a percentage. Set to 0 to disable it.");

    private ColorfulArmorBarConfig() {
    }

    public static void register() {
        SyrupConfigManager.getInstance().register(SPEC);
    }
}
