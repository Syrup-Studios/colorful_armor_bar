package net.syrupstudios.colorfularmorbar;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorfulArmorBar implements ModInitializer {
	public static final String MOD_ID = "colorful_armor_bar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Colorful Armor Bar initialized!");
	}
}