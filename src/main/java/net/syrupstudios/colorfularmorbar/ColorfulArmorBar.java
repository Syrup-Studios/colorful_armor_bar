package net.syrupstudios.colorfularmorbar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorfulArmorBar implements ClientModInitializer {
	public static final String MOD_ID = "colorful_armor_bar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Colorful Armor Bar initialized (Client-Side)!");

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
				.registerReloadListener(new ArmorBarRegistry());
	}
}