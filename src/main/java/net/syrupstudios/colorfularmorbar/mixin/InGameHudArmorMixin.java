package net.syrupstudios.colorfularmorbar.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.colorfularmorbar.ArmorBarRegistry;
import org.spongepowered.asm.mixin.Mixin;
//? if neoforge
/*import org.spongepowered.asm.mixin.Shadow;*/
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Arrays;

@Mixin(Gui.class)
public class InGameHudArmorMixin {

    @Unique
    //? if >=1.21 {
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.parse("hud/armor_empty");
    //?} else {
    /*private static final ResourceLocation VANILLA_GUI_ICONS = new ResourceLocation("textures/gui/icons.png");
     *///?}

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
        colorfulArmorBar$renderArmor(guiGraphics, guiGraphics.guiHeight() - leftHeight);
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
        colorfulArmorBar$renderArmor(guiGraphics, armorTop);
    }
    //?}

    @Unique
    private static void colorfulArmorBar$renderArmor(GuiGraphics guiGraphics, int armorTop) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        int armorValue = player.getArmorValue();
        if (armorValue <= 0) {
            return;
        }

        int left = guiGraphics.guiWidth() / 2 - 91;

        ResourceLocation[] points = new ResourceLocation[20];
        Arrays.fill(points, null);

        int pointIndex = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armorItem)) {
                continue;
            }

            ResourceLocation texture = ArmorBarRegistry.getTexture(armorItem, minecraft.getResourceManager());
            int protection = armorItem.getDefense();

            for (int p = 0; p < protection && pointIndex < Math.min(armorValue, points.length); p++) {
                points[pointIndex++] = texture;
            }
        }

        // Preserve the real armor total when another mod or an item-stack attribute
        // contributes points that cannot be assigned to a particular armor material.
        while (pointIndex < Math.min(armorValue, points.length)) {
            points[pointIndex++] = ArmorBarRegistry.FALLBACK_TEXTURE;
        }

        // 1.21.1 had a black bar behind the armor bar and this fixes it
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (int j = 0; j < 10; ++j) {
            int x = left + j * 8;

            //? if >=1.21 {
            guiGraphics.blitSprite(ARMOR_EMPTY_SPRITE, x, armorTop, 9, 9);
            //?} else {
            /*guiGraphics.blit(VANILLA_GUI_ICONS, x, armorTop, 16, 9, 9, 9);
            *///?}

            ResourceLocation leftTex = points[j * 2];
            ResourceLocation rightTex = points[j * 2 + 1];

            if (leftTex != null && leftTex.equals(rightTex)) {
                guiGraphics.blit(leftTex, x, armorTop, 0, 0, 9, 9, 18, 9);
            } else if (leftTex != null && rightTex == null) {
                guiGraphics.blit(leftTex, x, armorTop, 9, 0, 9, 9, 18, 9);
            } else {
                if (leftTex != null) {
                    guiGraphics.blit(leftTex, x, armorTop, 0, 0, 5, 9, 18, 9);
                }
                if (rightTex != null) {
                    guiGraphics.blit(rightTex, x + 5, armorTop, 5, 0, 4, 9, 18, 9);
                }
            }
        }
    }
}
