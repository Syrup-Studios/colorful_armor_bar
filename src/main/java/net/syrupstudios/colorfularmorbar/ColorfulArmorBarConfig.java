package net.syrupstudios.colorfularmorbar;

import net.syrupstudios.syruplibrary.config.ConfigSpec;
import net.syrupstudios.syruplibrary.config.SyrupConfigManager;
import net.syrupstudios.syruplibrary.config.value.BooleanConfigValue;
import net.syrupstudios.syruplibrary.config.value.IntConfigValue;

public final class ColorfulArmorBarConfig {
    private static final ConfigSpec SPEC = ConfigSpec.builder(ColorfulArmorBar.MOD_ID)
            .header("Colorful Armor Bar client configuration")
            .build();

    public static final BooleanConfigValue GROUP_MATCHING_ARMOR = SPEC.booleanValue(
            "group_matching_armor",
            false,
            "Combine matching armor materials and show the largest group first.");

    public static final IntConfigValue GLINT_OPACITY = SPEC.intValue(
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
