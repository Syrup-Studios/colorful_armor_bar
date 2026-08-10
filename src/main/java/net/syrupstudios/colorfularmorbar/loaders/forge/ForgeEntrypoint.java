package net.syrupstudios.colorfularmorbar.loaders.forge;

//? if forge {
/*import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.syrupstudios.colorfularmorbar.ArmorBarRegistry;
import net.syrupstudios.colorfularmorbar.ArmorBarRenderer;
import net.syrupstudios.colorfularmorbar.ColorfulArmorBar;

@Mod(ColorfulArmorBar.MOD_ID)
@Mod.EventBusSubscriber(modid = ColorfulArmorBar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeEntrypoint {
    public ForgeEntrypoint() {
        ColorfulArmorBar.initialize();
        ColorfulArmorBar.LOGGER.info("Colorful Armor Bar initialized (Forge client)");
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> ArmorBarRegistry.clearCache());
    }

    @Mod.EventBusSubscriber(modid = ColorfulArmorBar.MOD_ID, value = Dist.CLIENT)
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
}
*///?}
