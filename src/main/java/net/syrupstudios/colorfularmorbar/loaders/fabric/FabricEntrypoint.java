package net.syrupstudios.colorfularmorbar.loaders.fabric;

//? if fabric {
import net.minecraft.client.Minecraft;
import net.fabricmc.api.ClientModInitializer;
//? if >=26 {
/*import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
*///?} else {
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
//? if >=1.21.11 {
/*import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
//?}
import net.syrupstudios.colorfularmorbar.ArmorBarRegistry;
import net.syrupstudios.colorfularmorbar.ArmorBarRenderer;
import net.syrupstudios.colorfularmorbar.ColorfulArmorBar;

public class FabricEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorfulArmorBar.LOGGER.info("Colorful Armor Bar initialized (Fabric client)");
        //? if >=26 {
        /*Identifier reloadId = Identifier.fromNamespaceAndPath(ColorfulArmorBar.MOD_ID, "armor_bar_reload_listener");
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                reloadId, (ResourceManagerReloadListener) resourceManager -> ArmorBarRegistry.clearCache());
        registerModernFabricHud();
        *///?} else {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ReloadListener());
        //? if >=1.21.11
        /*registerModernFabricHud();*/
        //?}
    }

    //? if <26 {
    private static final class ReloadListener implements SimpleSynchronousResourceReloadListener {
        @Override
        //? if >=1.21.11 {
        /*public Identifier getFabricId() {
            return Identifier.fromNamespaceAndPath(ColorfulArmorBar.MOD_ID, "armor_bar_reload_listener");
        }
        *///?} else {
        public ResourceLocation getFabricId() {
            //? if >=1.21 {
            return ResourceLocation.fromNamespaceAndPath(ColorfulArmorBar.MOD_ID, "armor_bar_reload_listener");
            //?} else {
            /*return new ResourceLocation(ColorfulArmorBar.MOD_ID, "armor_bar_reload_listener");*/
            //?}
        }
        //?}

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            ArmorBarRegistry.clearCache();
        }
    }
    //?}

    //? if >=1.21.11 {
    /*private static void registerModernFabricHud() {
        HudElementRegistry.replaceElement(VanillaHudElements.ARMOR_BAR, original -> (graphics, tickCounter) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.player.getArmorValue() <= 0) {
                return;
            }
            int top = graphics.guiHeight() - HudStatusBarHeightRegistry.getHeight(VanillaHudElements.ARMOR_BAR);
            ArmorBarRenderer.render(graphics, top);
        });
    }
    *///?}
}
//?}
