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

    private ColorfulArmorBarConfig() {
    }

    public static void register() {
        SyrupConfigManager.getInstance().register(SPEC);
    }
}
