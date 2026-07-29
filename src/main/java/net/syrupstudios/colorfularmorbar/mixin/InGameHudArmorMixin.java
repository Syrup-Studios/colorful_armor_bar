package net.syrupstudios.colorfularmorbar.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.syrupstudios.colorfularmorbar.ArmorBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
//? if neoforge
/*import org.spongepowered.asm.mixin.Shadow;*/
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudArmorMixin {

    //? if >=1.21 {
    @Redirect(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"))
    private static int colorfulArmorBar$skipVanillaArmor(Player instance) {
        return 0;
    }
    //?} else {
    /*@Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"))
    private int colorfulArmorBar$skipVanillaArmor(Player instance) {
        return 0;
    }
    *///?}

    // NeoForge splits the HUD into independent layers and tracks their vertical
    // position with leftHeight. Hook its armor layer after health has advanced it.
    //? if neoforge {
    /*@Shadow
    private int leftHeight;

    @Inject(method = "renderArmorLevel", at = @At("HEAD"))
    private void colorfulArmorBar$renderNeoForgeArmor(GuiGraphics guiGraphics, CallbackInfo ci) {
        ArmorBarRenderer.render(guiGraphics, guiGraphics.guiHeight() - leftHeight);
    }
    *///?} else {
    @Inject(method = "renderPlayerHealth", at = @At("HEAD"))
    private void colorfulArmorBar$renderVanillaArmor(GuiGraphics guiGraphics, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || player.getArmorValue() <= 0) {
            return;
        }

        int top = guiGraphics.guiHeight() - 39;
        int maxHealth = (int) Math.ceil(player.getMaxHealth());
        int absorption = (int) Math.ceil(player.getAbsorptionAmount());
        int healthRows = (int) Math.ceil((maxHealth + absorption) / 20.0);
        int rowSpacing = Math.max(10 - (healthRows - 2), 3);
        int armorTop = top - (healthRows - 1) * rowSpacing - 10;
        ArmorBarRenderer.render(guiGraphics, armorTop);
    }
    //?}
}
