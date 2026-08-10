package net.syrupstudios.colorfularmorbar;

import net.syrupstudios.syruplibrary.config.ConfigSpec;
import net.syrupstudios.syruplibrary.config.SyrupConfigManager;
import net.syrupstudios.syruplibrary.config.value.BooleanConfigValue;

public final class ColorfulArmorBarConfig {
    private static final ConfigSpec SPEC = ConfigSpec.builder(ColorfulArmorBar.MOD_ID)
            .header("Colorful Armor Bar client configuration")
            .build();

    public static final BooleanConfigValue GROUP_MATCHING_ARMOR = SPEC.booleanValue(
            "group_matching_armor",
            false,
            "Combine matching armor materials and show the largest group first.");

    private ColorfulArmorBarConfig() {
    }

    public static void register() {
        SyrupConfigManager.getInstance().register(SPEC);
    }
}
