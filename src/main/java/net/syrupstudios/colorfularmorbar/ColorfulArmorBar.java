package net.syrupstudios.colorfularmorbar;

import net.minecraft.client.Minecraft;
//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
//?} elif forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
*///?} elif neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
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
        LOGGER.info("Colorful Armor Bar initialized (Forge client; HUD overlay and reload listener registered)");
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
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ReloadListener());
    }

    private static final class ReloadListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            //? if >=1.21 {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, "armor_bar_reload_listener");
            //?} else {
            /*return new ResourceLocation(MOD_ID, "armor_bar_reload_listener");*/
            //?}
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            ArmorBarRegistry.clearCache();
        }
    }
    //?} else {
    /*@SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> {
            ArmorBarRegistry.clearCache();
        });
    }*/
    //?}
}
