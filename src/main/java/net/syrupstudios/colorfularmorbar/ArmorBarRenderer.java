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
    private static final int GLINT_TEXTURE_SIZE = 128;
    private static final int GLINT_TEXTURE_SCALE = 4;
    private static final int GLINT_SCROLL_PERIOD = 64;

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    //? if >=1.21.11 {
    /*private static final Identifier ARMOR_EMPTY_SPRITE = Identifier.parse("hud/armor_empty");
    private static final Identifier ENCHANTED_GLINT_TEXTURE = Identifier.parse("textures/misc/enchanted_glint_item.png");
    *///?} elif >=1.21 {
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.parse("hud/armor_empty");
    private static final ResourceLocation ENCHANTED_GLINT_TEXTURE = ResourceLocation.parse("textures/misc/enchanted_glint_item.png");
    //?} else {
    /*private static final ResourceLocation VANILLA_GUI_ICONS = new ResourceLocation("textures/gui/icons.png");
    private static final ResourceLocation ENCHANTED_GLINT_TEXTURE = new ResourceLocation("textures/misc/enchanted_glint_item.png");
    *///?}

    private ArmorBarRenderer() {
    }

    //? if >=1.21.11 {
    /*private record ArmorPoint(Identifier texture, boolean enchanted, boolean[] alphaMask) {
    }

    private record GlintSegment(boolean[] alphaMask, int x, int u, int width) {
    }
    *///?} else {
    private record ArmorPoint(ResourceLocation texture, boolean enchanted, boolean[] alphaMask) {
    }

    private record GlintSegment(boolean[] alphaMask, int x, int u, int width) {
    }
    //?}

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

        List<ArmorBarLayout.Contribution<ArmorPoint>> contributions = new ArrayList<>();

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
                        createArmorPoint(stack, minecraft),
                        points));
            }
            *///?} else {
            if (stack.getItem() instanceof ArmorItem armorItem) {
                contributions.add(new ArmorBarLayout.Contribution<>(
                        createArmorPoint(stack, minecraft),
                        armorItem.getDefense()));
            }
            //?}
        }

        List<ArmorPoint> points = ArmorBarLayout.build(
                player.getArmorValue(), contributions, new ArmorPoint(ArmorBarRegistry.FALLBACK_TEXTURE, false, null),
                ColorfulArmorBarConfig.GROUP_MATCHING_ARMOR.get());
        List<GlintSegment> glintSegments = new ArrayList<>();

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

            ArmorPoint leftPoint = points.get(j * 2);
            ArmorPoint rightPoint = points.get(j * 2 + 1);

            if (leftPoint != null && leftPoint.equals(rightPoint)) {
                blitPoint(guiGraphics, leftPoint, x, armorTop, 0, 9, glintSegments);
            } else if (leftPoint != null && rightPoint == null) {
                blitPoint(guiGraphics, leftPoint, x, armorTop, 9, 9, glintSegments);
            } else {
                if (leftPoint != null) {
                    blitPoint(guiGraphics, leftPoint, x, armorTop, 0, 5, glintSegments);
                }
                if (rightPoint != null) {
                    blitPoint(guiGraphics, rightPoint, x + 5, armorTop, 5, 4, glintSegments);
                }
            }
        }

        renderGlint(guiGraphics, glintSegments, armorTop);
    }

    private static ArmorPoint createArmorPoint(ItemStack stack, Minecraft minecraft) {
        var texture = ArmorBarRegistry.getTexture(stack, minecraft.getResourceManager());
        boolean enchanted = stack.hasFoil();
        boolean[] alphaMask = enchanted
                ? ArmorBarRegistry.getAlphaMask(texture, minecraft.getResourceManager())
                : null;
        return new ArmorPoint(texture, enchanted, alphaMask);
    }

    //? if >=26 {
    /*private static void blitPoint(GuiGraphicsExtractor guiGraphics, ArmorPoint point, int x, int y, int u, int width,
                                  List<GlintSegment> glintSegments) {
    *///?} else {
    private static void blitPoint(GuiGraphics guiGraphics, ArmorPoint point, int x, int y, int u, int width,
                                  List<GlintSegment> glintSegments) {
    //?}
        blit(guiGraphics, point.texture(), x, y, u, 0, width, 9);
        if (point.enchanted()) {
            glintSegments.add(new GlintSegment(point.alphaMask(), x, u, width));
        }
    }

    //? if >=26 {
    /*private static void renderGlint(GuiGraphicsExtractor guiGraphics, List<GlintSegment> segments, int y) {
    *///?} else {
    private static void renderGlint(GuiGraphics guiGraphics, List<GlintSegment> segments, int y) {
    //?}
        if (segments.isEmpty()) {
            return;
        }

        float opacity = ColorfulArmorBarConfig.GLINT_OPACITY.get() / 100.0F;
        if (opacity <= 0.0F) {
            return;
        }
        int glintColor = Math.round(opacity * 255.0F) << 24 | 0xFFFFFF;

        // Older GUI renderers use a global alpha. Flush around it so the alpha
        // only applies to the vanilla glint texture.
        //? if <1.21.11 {
        guiGraphics.flush();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, opacity);
        //?}

        int phase = (int) ((System.currentTimeMillis() / 40L) % GLINT_SCROLL_PERIOD);
        for (GlintSegment segment : segments) {
            for (int row = 0; row < 9; row++) {
                int column = 0;
                while (column < segment.width()) {
                    while (column < segment.width() && !isOpaque(segment, column, row)) {
                        column++;
                    }
                    int start = column;
                    while (column < segment.width() && isOpaque(segment, column, row)) {
                        column++;
                    }
                    if (start < column) {
                        int glintU = Math.floorMod(
                                (segment.x() + start) * GLINT_TEXTURE_SCALE + phase,
                                GLINT_SCROLL_PERIOD);
                        int glintV = Math.floorMod(
                                (y + row) * GLINT_TEXTURE_SCALE + phase / 2,
                                GLINT_SCROLL_PERIOD);
                        blitGlint(guiGraphics, segment.x() + start, y + row, glintU, glintV,
                                column - start, glintColor);
                    }
                }
            }
        }

        //? if <1.21.11 {
        guiGraphics.flush();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //?}
    }

    private static boolean isOpaque(GlintSegment segment, int column, int row) {
        return segment.alphaMask()[row * 18 + segment.u() + column];
    }

    //? if >=26 {
    /*private static void blit(GuiGraphicsExtractor guiGraphics, Identifier texture, int x, int y, int u, int v,
                             int width, int height) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 18, 9);
    }

    private static void blitGlint(GuiGraphicsExtractor guiGraphics, int x, int y, int u, int v, int width,
                                  int color) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTED_GLINT_TEXTURE, x, y, u, v, width, 1,
                width * GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SIZE, GLINT_TEXTURE_SIZE, color);
    }
    *///?} elif >=1.21.11 {
    /*private static void blit(GuiGraphics guiGraphics, Identifier texture, int x, int y, int u, int v,
                             int width, int height) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 18, 9);
    }

    private static void blitGlint(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int color) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTED_GLINT_TEXTURE, x, y, u, v, width, 1,
                width * GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SIZE, GLINT_TEXTURE_SIZE, color);
    }
    *///?} else {
    private static void blit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v,
                             int width, int height) {
        guiGraphics.blit(texture, x, y, u, v, width, height, 18, 9);
    }

    private static void blitGlint(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int color) {
        guiGraphics.blit(ENCHANTED_GLINT_TEXTURE, x, y, width, 1, (float) u, (float) v,
                width * GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SIZE, GLINT_TEXTURE_SIZE);
    }
    //?}
}
