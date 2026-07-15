package net.syrupstudios.colorfularmorbar;

import net.minecraft.client.Minecraft;
//? if fabric {
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
//?} elif forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
*///?} elif neoforge && >=1.21.11 {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
*///?} elif neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
*///?}
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if forge
/*@Mod(ColorfulArmorBar.MOD_ID)*/
//? if neoforge
/*@Mod(value = ColorfulArmorBar.MOD_ID, dist = Dist.CLIENT)*/
//? if forge
/*@Mod.EventBusSubscriber(modid = ColorfulArmorBar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)*/
//? if neoforge
/*@EventBusSubscriber(modid = ColorfulArmorBar.MOD_ID, value = Dist.CLIENT)*/
public class ColorfulArmorBar
//? if fabric {
        implements ClientModInitializer {
//?} else {
/*{*/
//?}
    public static final String MOD_ID = "colorful_armor_bar";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //? if forge {
    /*public ColorfulArmorBar() {
        LOGGER.info("Colorful Armor Bar initialized (Forge client)");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    private static final class ForgeClientEvents {
        @SubscribeEvent
        public static void renderArmorOverlay(RenderGuiOverlayEvent.Pre event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (event.getOverlay() != VanillaGuiOverlay.ARMOR_LEVEL.type()
                    || minecraft.player == null
                    || minecraft.player.getArmorValue() <= 0
                    || minecraft.options.hideGui
                    || !(minecraft.gui instanceof ForgeGui forgeGui)
                    || !forgeGui.shouldDrawSurvivalElements()) {
                return;
            }

            event.setCanceled(true);
            forgeGui.setupOverlayRenderState(true, false);
            ArmorBarRenderer.render(event.getGuiGraphics(), event.getWindow().getGuiScaledHeight() - forgeGui.leftHeight);
            forgeGui.leftHeight += 10;
        }
    }
    *///?}

    //? if fabric {
    @Override
    public void onInitializeClient() {
        LOGGER.info("Colorful Armor Bar initialized (Fabric client)");
        //? if >=26 {
        /*Identifier reloadId = Identifier.fromNamespaceAndPath(MOD_ID, "armor_bar_reload_listener");
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
            return Identifier.fromNamespaceAndPath(MOD_ID, "armor_bar_reload_listener");
        }
        *///?} else {
        public ResourceLocation getFabricId() {
            //? if >=1.21 {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, "armor_bar_reload_listener");
            //?} else {
            /*return new ResourceLocation(MOD_ID, "armor_bar_reload_listener");*/
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
    //?} elif legacyevent {
    /*@SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> ArmorBarRegistry.clearCache());
    }
    *///?}

    //? if modernneo {
    /*@SubscribeEvent
    public static void addReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(MOD_ID, "armor_bar_reload_listener"),
                (ResourceManagerReloadListener) resourceManager -> ArmorBarRegistry.clearCache());
    }

    *///?}

    // Keep these as two flat Stonecutter branches. Nesting a version branch inside
    // a disabled loader branch would require nested Java block comments.
    //? if modernneo && >=26 {
    /*@SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.replaceLayer(VanillaGuiLayers.ARMOR_LEVEL, (graphics, deltaTracker) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.player.getArmorValue() <= 0) {
                return;
            }
            int top = graphics.guiHeight() - minecraft.gui.hud.leftHeight;
            ArmorBarRenderer.render(graphics, top);
            minecraft.gui.hud.leftHeight += 10;
        });
    }
    *///?} elif modernneo {
    /*@SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.replaceLayer(VanillaGuiLayers.ARMOR_LEVEL, (graphics, deltaTracker) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.player.getArmorValue() <= 0) {
                return;
            }
            int top = graphics.guiHeight() - minecraft.gui.leftHeight;
            ArmorBarRenderer.render(graphics, top);
            minecraft.gui.leftHeight += 10;
        });
    }
    *///?}
}
