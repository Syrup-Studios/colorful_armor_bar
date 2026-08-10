package net.syrupstudios.colorfularmorbar;

//? if <1.21.11
import com.mojang.blaze3d.systems.RenderSystem;
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;
*///?} else {
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
//?}
import net.minecraft.client.Minecraft;
//? if >=26 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ArmorBarRenderer {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    //? if >=1.21.11 {
    /*private static final Identifier ARMOR_EMPTY_SPRITE = Identifier.parse("hud/armor_empty");
    *///?} elif >=1.21 {
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.parse("hud/armor_empty");
    //?} else {
    /*private static final ResourceLocation VANILLA_GUI_ICONS = new ResourceLocation("textures/gui/icons.png");
    *///?}

    private ArmorBarRenderer() {
    }

    //? if >=26 {
    /*public static void render(GuiGraphicsExtractor guiGraphics, int armorTop) {
    *///?} else {
    public static void render(GuiGraphics guiGraphics, int armorTop) {
    //?}
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || player.getArmorValue() <= 0) {
            return;
        }

        //? if >=1.21.11 {
        /*List<ArmorBarLayout.Contribution<Identifier>> contributions = new ArrayList<>();
        *///?} else {
        List<ArmorBarLayout.Contribution<ResourceLocation>> contributions = new ArrayList<>();
        //?}

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            //? if >=1.21.11 {
            /*Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (equippable == null || equippable.slot() != slot || modifiers == null) {
                continue;
            }
            int points = Math.max(0, (int)Math.round(modifiers.compute(Attributes.ARMOR, 0.0, slot)));
            if (points > 0) {
                contributions.add(new ArmorBarLayout.Contribution<>(
                        ArmorBarRegistry.getTexture(stack, minecraft.getResourceManager()), points));
            }
            *///?} else {
            if (stack.getItem() instanceof ArmorItem armorItem) {
                contributions.add(new ArmorBarLayout.Contribution<>(
                        ArmorBarRegistry.getTexture(stack, minecraft.getResourceManager()), armorItem.getDefense()));
            }
            //?}
        }

        //? if >=1.21.11 {
        /*List<Identifier> points = ArmorBarLayout.build(
                player.getArmorValue(), contributions, ArmorBarRegistry.FALLBACK_TEXTURE,
                ColorfulArmorBarConfig.GROUP_MATCHING_ARMOR.get());
        *///?} else {
        List<ResourceLocation> points = ArmorBarLayout.build(
                player.getArmorValue(), contributions, ArmorBarRegistry.FALLBACK_TEXTURE,
                ColorfulArmorBarConfig.GROUP_MATCHING_ARMOR.get());
        //?}

        int left = guiGraphics.guiWidth() / 2 - 91;

        // 1.21.1 needs explicit blending to avoid a black bar behind the icons.
        //? if <1.21.11 {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        //?}

        for (int j = 0; j < 10; j++) {
            int x = left + j * 8;

            //? if >=1.21.11 {
            /*guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_SPRITE, x, armorTop, 9, 9);
            *///?} elif >=1.21 {
            guiGraphics.blitSprite(ARMOR_EMPTY_SPRITE, x, armorTop, 9, 9);
            //?} else {
            /*guiGraphics.blit(VANILLA_GUI_ICONS, x, armorTop, 16, 9, 9, 9);
            *///?}

            //? if >=1.21.11 {
            /*Identifier leftTexture = points.get(j * 2);
            Identifier rightTexture = points.get(j * 2 + 1);
            *///?} else {
            ResourceLocation leftTexture = points.get(j * 2);
            ResourceLocation rightTexture = points.get(j * 2 + 1);
            //?}

            if (leftTexture != null && leftTexture.equals(rightTexture)) {
                blit(guiGraphics, leftTexture, x, armorTop, 0, 9);
            } else if (leftTexture != null && rightTexture == null) {
                blit(guiGraphics, leftTexture, x, armorTop, 9, 9);
            } else {
                if (leftTexture != null) {
                    blit(guiGraphics, leftTexture, x, armorTop, 0, 5);
                }
                if (rightTexture != null) {
                    blit(guiGraphics, rightTexture, x + 5, armorTop, 5, 4);
                }
            }
        }
    }

    //? if >=26 {
    /*private static void blit(GuiGraphicsExtractor guiGraphics, Identifier texture, int x, int y, int u, int width) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, 0, width, 9, 18, 9);
    }
    *///?} elif >=1.21.11 {
    /*private static void blit(GuiGraphics guiGraphics, Identifier texture, int x, int y, int u, int width) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, 0, width, 9, 18, 9);
    }
    *///?} else {
    private static void blit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int width) {
        guiGraphics.blit(texture, x, y, u, 0, width, 9, 18, 9);
    }
    //?}
}
