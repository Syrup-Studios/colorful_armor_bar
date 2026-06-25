package net.syrupstudios.colorfularmorbar.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class ColorfulArmorBarClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ArmorBarRegistry());
	}
}