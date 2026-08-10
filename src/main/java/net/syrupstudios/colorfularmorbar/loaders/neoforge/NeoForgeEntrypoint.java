package net.syrupstudios.colorfularmorbar.loaders.neoforge;

//? if neoforge {
/*import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.syrupstudios.colorfularmorbar.ArmorBarRegistry;
import net.syrupstudios.colorfularmorbar.ArmorBarRenderer;
import net.syrupstudios.colorfularmorbar.ColorfulArmorBar;
//? if >=1.21.11 {
/^import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
^///?} else {
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
//?}

@Mod(value = ColorfulArmorBar.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ColorfulArmorBar.MOD_ID, value = Dist.CLIENT)
public class NeoForgeEntrypoint {

    public NeoForgeEntrypoint() {
        ColorfulArmorBar.initialize();
        ColorfulArmorBar.LOGGER.info("Colorful Armor Bar initialized (NeoForge client)");
    }

    //? if >=1.21.11 {
    /^@SubscribeEvent
    public static void addReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(ColorfulArmorBar.MOD_ID, "armor_bar_reload_listener"),
                (ResourceManagerReloadListener) resourceManager -> ArmorBarRegistry.clearCache());
    }
    ^///?} else {
    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> ArmorBarRegistry.clearCache());
    }
    //?}

    //? if >=26 {
    /^@SubscribeEvent
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
    ^///?} elif >=1.21.11 {
    /^@SubscribeEvent
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
    ^///?}
}
*///?}
