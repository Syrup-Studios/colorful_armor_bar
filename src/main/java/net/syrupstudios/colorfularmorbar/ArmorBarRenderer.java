package net.syrupstudios.colorfularmorbar;

//? if >=1.21.11 {
/*import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//? if <26.2 {
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
//?} elif <26.3 {
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
//?} else {
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
//?}
*///?} else {
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
//?}
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
*///?} else {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.ArmorTrim;
//?}
import net.minecraft.client.Minecraft;
//? if >=1.21
import net.minecraft.core.component.DataComponents;
//? if >=1.21
import net.minecraft.world.item.component.DyedItemColor;
//? if >=1.21.11 {
/*
import net.minecraft.world.item.equipment.Equippable;
*///?}
//? if <1.21 {
/*
import net.minecraft.world.item.DyeableLeatherItem;
*///?}
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ArmorBarRenderer {
    private static final int GLINT_TEXTURE_SIZE = 128;
    private static final int GLINT_TEXTURE_SCALE = 4;
    private static final int GLINT_SCROLL_PERIOD = 64;
    private static final long TRANSITION_DURATION_NANOS = 220_000_000L;
    private static final long STALE_ARMOR_VALUE_TIMEOUT_NANOS = 1_000_000_000L;

//? if >=1.21.11 {
/*    //? if <26.2 {
    private static final RenderPipeline GLINT_PIPELINE = RenderPipeline.builder()
            .withLocation("pipeline/colorful_armor_bar/gui_glint")
            .withUniform("DynamicTransforms", com.mojang.blaze3d.shaders.UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", com.mojang.blaze3d.shaders.UniformType.UNIFORM_BUFFER)
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withSampler("Sampler0")
            .withBlend(BlendFunction.GLINT)
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .withDepthTestFunction(com.mojang.blaze3d.platform.DepthTestFunction.NO_DEPTH_TEST)
            .build();
    //?} elif <26.3 {
    private static final RenderPipeline GLINT_PIPELINE = RenderPipeline.builder()
            .withLocation("pipeline/colorful_armor_bar/gui_glint")
            .withBindGroupLayout(BindGroupLayouts.GLOBALS)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build();
    //?} else {
    private static final RenderPipeline GLINT_PIPELINE = RenderPipeline.builder()
            .withLocation("pipeline/colorful_armor_bar/gui_glint")
            .withBindGroupLayout(BindGroupLayouts.GLOBALS)
            .withBindGroupLayout(BindGroupLayouts.PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build();
    //?}
*///?}

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    //? if >=1.21 {
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.parse("hud/armor_empty");
    private static final ResourceLocation ENCHANTED_GLINT_TEXTURE = ResourceLocation.parse("textures/misc/enchanted_glint_item.png");
    //?} else {
    /*private static final ResourceLocation VANILLA_GUI_ICONS = new ResourceLocation("textures/gui/icons.png");
    private static final ResourceLocation ENCHANTED_GLINT_TEXTURE = new ResourceLocation("textures/misc/enchanted_glint_item.png");
    *///?}

    private ArmorBarRenderer() {
    }

    private record ArmorPoint(ResourceLocation texture, boolean enchanted, boolean[] alphaMask, Integer leatherColor, Integer trimColor,
                              boolean[] trimMask, boolean glowingTrim, EquipmentSlot slot) {
        private ArmorPoint visualKey() {
            return new ArmorPoint(texture, enchanted, alphaMask, leatherColor, trimColor, trimMask, glowingTrim, null);
        }
    }

    private record GlintSegment(boolean[] alphaMask, int x, int u, int width, int clipStart, int clipEnd) {
    }
    private static Player lastPlayer;
    private static List<ArmorPoint> previousPoints;
    private static List<ArmorPoint> currentPoints;
    private static int effectiveArmorTotal;
    private static int lastContributionTotal;
    private static long staleArmorValueStart;
    private static long transitionStart;

    public static boolean render(GuiGraphics guiGraphics, int armorTop) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        ArmorBarFeedback.update(player);
        if (player == null) {
            lastPlayer = null;
            previousPoints = currentPoints = null;
            effectiveArmorTotal = lastContributionTotal = 0;
            staleArmorValueStart = 0L;
            return false;
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
                ArmorPoint point = createArmorPoint(stack, minecraft, slot);
                contributions.add(new ArmorBarLayout.Contribution<>(
                        point, points, point.visualKey()));
            }
            *///?} else {
            if (stack.getItem() instanceof ArmorItem armorItem) {
                ArmorPoint point = createArmorPoint(stack, minecraft, slot);
                contributions.add(new ArmorBarLayout.Contribution<>(
                        point, armorItem.getDefense(), point.visualKey()));
            }
            //?}
        }

        int rawArmorTotal = Math.max(0, player.getArmorValue());
        int contributedPoints = contributions.stream().mapToInt(contribution -> contribution.points()).sum();
        long now = System.nanoTime();
        boolean playerChanged = player != lastPlayer || currentPoints == null;
        if (playerChanged) {
            effectiveArmorTotal = rawArmorTotal;
            lastContributionTotal = contributedPoints;
            staleArmorValueStart = 0L;
        } else {
            int contributionDelta = contributedPoints - lastContributionTotal;
            if (staleArmorValueStart != 0L) {
                effectiveArmorTotal = Math.max(0, effectiveArmorTotal + contributionDelta);
                if (rawArmorTotal <= effectiveArmorTotal
                        || now - staleArmorValueStart >= STALE_ARMOR_VALUE_TIMEOUT_NANOS) {
                    effectiveArmorTotal = rawArmorTotal;
                    staleArmorValueStart = 0L;
                }
            } else if (contributionDelta < 0) {
                int expectedArmorTotal = Math.max(0, effectiveArmorTotal + contributionDelta);
                if (rawArmorTotal > expectedArmorTotal) {
                    // Equipment updates can arrive before the armor attribute total.
                    effectiveArmorTotal = expectedArmorTotal;
                    staleArmorValueStart = now;
                } else {
                    effectiveArmorTotal = rawArmorTotal;
                }
            } else {
                effectiveArmorTotal = rawArmorTotal;
            }
            lastContributionTotal = contributedPoints;
        }
        List<ArmorPoint> points = ArmorBarLayout.build(
                effectiveArmorTotal, contributions,
                new ArmorPoint(ArmorBarRegistry.FALLBACK_TEXTURE, false,
                        ArmorBarRegistry.getAlphaMask(ArmorBarRegistry.FALLBACK_TEXTURE, minecraft.getResourceManager()),
                        null, null, null, false, null),
                ColorfulArmorBarConfig.GROUP_MATCHING_ARMOR.get());
        List<ArmorPoint> renderPoints;
        if (playerChanged || !ColorfulArmorBarConfig.ARMOR_ICON_ANIMATION.get()) {
            lastPlayer = player;
            previousPoints = currentPoints = points;
            transitionStart = 0L;
            renderPoints = points;
        } else if (points.equals(currentPoints)) {
            renderPoints = currentPoints;
        } else {
            previousPoints = currentPoints;
            currentPoints = points;
            transitionStart = now;
            renderPoints = currentPoints;
        }
        float transition = transitionStart == 0L ? 1.0F
                : Math.min(1.0F, (float) (now - transitionStart) / TRANSITION_DURATION_NANOS);
        boolean animating = transition < 1.0F && previousPoints != currentPoints;
        if (!animating && player.getArmorValue() <= 0) {
            return false;
        }
        List<GlintSegment> glintSegments = new ArrayList<>();

        int left = guiGraphics.guiWidth() / 2 - 91;

        // 1.21.1 needs explicit blending to avoid a black bar behind the icons.
        //? if <1.21.11 {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        //?}

        for (int j = 0; j < 10; j++) {
            int x = left + j * 8;

            ArmorPoint oldLeft = previousPoints.get(j * 2);
            ArmorPoint oldRight = previousPoints.get(j * 2 + 1);
            ArmorPoint newLeft = renderPoints.get(j * 2);
            ArmorPoint newRight = renderPoints.get(j * 2 + 1);
            if (!animating || (oldLeft == null ? newLeft == null : oldLeft.equals(newLeft))
                    && (oldRight == null ? newRight == null : oldRight.equals(newRight))) {
                drawIcon(guiGraphics, x, armorTop, newLeft, newRight, glintSegments, -1, -1);
                continue;
            }

            int reveal = Math.round(9.0F * transition);
            if (reveal < 9) {
                guiGraphics.enableScissor(x + reveal, armorTop, x + 9, armorTop + 9);
                try {
                    drawIcon(guiGraphics, x, armorTop, oldLeft, oldRight, glintSegments, x + reveal, x + 9);
                } finally {
                    guiGraphics.disableScissor();
                }
            }
            if (reveal > 0) {
                guiGraphics.enableScissor(x, armorTop, x + reveal, armorTop + 9);
                try {
                    drawIcon(guiGraphics, x, armorTop, newLeft, newRight, glintSegments, x, x + reveal);
                } finally {
                    guiGraphics.disableScissor();
                }
            }
        }

        renderGlint(guiGraphics, glintSegments, armorTop);
        return true;
    }

    private static void drawIcon(GuiGraphics guiGraphics, int x, int y, ArmorPoint leftPoint, ArmorPoint rightPoint,
                                 List<GlintSegment> glintSegments, int clipStart, int clipEnd) {
        //? if >=1.21.11 {
        /*guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_SPRITE, x, y, 9, 9);
        *///?} elif >=1.21 {
        guiGraphics.blitSprite(ARMOR_EMPTY_SPRITE, x, y, 9, 9);
        //?} else {
        /*guiGraphics.blit(VANILLA_GUI_ICONS, x, y, 16, 9, 9, 9);
        *///?}
        if (leftPoint != null && leftPoint.equals(rightPoint)) {
            blitPoint(guiGraphics, leftPoint, x, y, 0, 9, glintSegments, clipStart, clipEnd);
        } else if (leftPoint != null && rightPoint == null) {
            blitPoint(guiGraphics, leftPoint, x, y, 9, 9, glintSegments, clipStart, clipEnd);
        } else {
            if (leftPoint != null) {
                blitPoint(guiGraphics, leftPoint, x, y, 0, 5, glintSegments, clipStart, clipEnd);
            }
            if (rightPoint != null) {
                blitPoint(guiGraphics, rightPoint, x + 5, y, 5, 4, glintSegments, clipStart, clipEnd);
            }
        }
    }

    private static ArmorPoint createArmorPoint(ItemStack stack, Minecraft minecraft, EquipmentSlot slot) {
        var texture = ArmorBarRegistry.getTexture(stack, minecraft.getResourceManager());
        boolean enchanted = stack.hasFoil();
        ArmorTrim trim = getTrim(stack, minecraft);
        boolean[] alphaMask = ArmorBarRegistry.getAlphaMask(texture, minecraft.getResourceManager());
        Integer leatherColor = getLeatherColor(stack);
        Integer trimColor = trim == null ? null : getTrimColor(trim);
        boolean[] trimMask = trim == null
                ? null
                : ArmorBarRegistry.getTrimAlphaMask(
                        trim.pattern().value().assetId(), minecraft.getResourceManager());
        String trimMaterial = trim == null ? "" : getTrimMaterialName(trim);
        boolean glowingTrim = trimMaterial.equals("amethyst") || trimMaterial.equals("diamond") || trimMaterial.equals("emerald");
        return new ArmorPoint(texture, enchanted, alphaMask, leatherColor, trimColor, trimMask, glowingTrim, slot);
    }

    private static Integer getLeatherColor(ItemStack stack) {
        //? if >=1.21.11 {
        /*
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (equippable == null || !itemId.getPath().startsWith("leather_")
                && !equippable.assetId().map(key -> key.identifier().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "leather"))).orElse(false)) {
            return null;
        }
        DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
        return dyedColor == null ? DyedItemColor.LEATHER_COLOR : dyedColor.rgb();
        *///?} elif >=1.21 {
        if (!(stack.getItem() instanceof ArmorItem armorItem)
                || armorItem.getMaterial().unwrapKey().map(key -> !key.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "leather"))).orElse(true)) {
            return null;
        }
        DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
        return dyedColor == null ? DyedItemColor.LEATHER_COLOR : dyedColor.rgb();
        //?} else {
        /*
        return stack.getItem() instanceof DyeableLeatherItem leather ? leather.getColor(stack) : null;
        *///?}
    }

    private static ArmorTrim getTrim(ItemStack stack, Minecraft minecraft) {
        //? if >=1.21 {
        return stack.get(DataComponents.TRIM);
        //?} else {
        /*return minecraft.level == null
                ? null
                : ArmorTrim.getTrim(minecraft.level.registryAccess(), stack).orElse(null);
        *///?}
    }

    private static int getTrimColor(ArmorTrim trim) {
        var color = trim.material().value().description().getStyle().getColor();
        return color == null ? 0xFFFFFF : color.getValue();
    }

    private static String getTrimMaterialName(ArmorTrim trim) {
        //? if >=1.21.11-rc2 {
        /*return trim.material().unwrapKey().map(key -> key.identifier().getPath()).orElse("");
        *///?} else {
        return trim.material().unwrapKey().map(key -> key.location().getPath()).orElse("");
        //?}
    }

    private static void blitPoint(GuiGraphics guiGraphics, ArmorPoint point, int x, int y, int u, int width,
                                  List<GlintSegment> glintSegments, int clipStart, int clipEnd) {
        if (point.leatherColor() != null) {
            blitTinted(guiGraphics, point.texture(), x, y, u, 0, width, 9, point.leatherColor());
        } else {
            blit(guiGraphics, point.texture(), x, y, u, 0, width, 9);
        }
        if (point.trimColor() != null) {
            renderTrimIcon(guiGraphics, x, y, u, width, point.trimColor(), point.trimMask(), point.alphaMask(),
                    point.glowingTrim() && ColorfulArmorBarConfig.TRIM_GLOW.get());
        }
        ArmorBarFeedback.render(guiGraphics, point.slot(), point.alphaMask(), x, y, u, width);
        if (point.enchanted()) {
            int start = clipStart < 0 ? 0 : Math.max(0, clipStart - x);
            int end = clipEnd < 0 ? width : Math.min(width, clipEnd - x);
            if (start < end) {
                glintSegments.add(new GlintSegment(point.alphaMask(), x, u, width, start, end));
            }
        }
    }

    private static void renderTrimIcon(GuiGraphics guiGraphics, int x, int y, int u, int width, int trimColor,
                                       boolean[] trimMask, boolean[] alphaMask, boolean glowing) {
        int sourceX = u >= 9 ? 0 : u;
        int argb = 0xFF000000 | trimColor;
        int glowAlpha = 0x20 + Math.round((float) (Math.sin(System.currentTimeMillis() / 500.0) + 1.0) * 16.0F);
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < width; column++) {
                int iconX = sourceX + column;
                if (trimMask[row * 9 + iconX] && alphaMask[row * 18 + u + column]) {
                    guiGraphics.fill(x + column, y + row, x + column + 1, y + row + 1, argb);
                    if (glowing) {
                        guiGraphics.fill(x + column, y + row, x + column + 1, y + row + 1, glowAlpha << 24 | 0xFFFFFF);
                    }
                }
            }
        }
    }

    private static void renderGlint(GuiGraphics guiGraphics, List<GlintSegment> segments, int y) {
        if (segments.isEmpty()) {
            return;
        }

        float opacity = Minecraft.getInstance().options.glintStrength().get().floatValue();
        if (opacity <= 0.0F) {
            return;
        }
        int glintTint = Math.round((float) Math.sqrt(opacity) * 255.0F);
        int glintColor = 0xFF000000 | glintTint << 16 | glintTint << 8 | glintTint;

        // Older GUI renderers use global color and blend state. Flush around the
        // glint pass so those changes only affect the glint texture.
        //? if <1.21.11 {
        guiGraphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        float tint = glintTint / 255.0F;
        guiGraphics.setColor(tint, tint, tint, 1.0F);
        //?}

        double glintSpeed = Minecraft.getInstance().options.glintSpeed().get();
        int phase = (int) ((System.currentTimeMillis() / 40.0 * glintSpeed) % GLINT_SCROLL_PERIOD);
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
        RenderSystem.defaultBlendFunc();
        //?}
    }

    private static boolean isOpaque(GlintSegment segment, int column, int row) {
        return column >= segment.clipStart() && column < segment.clipEnd()
                && segment.alphaMask()[row * 18 + segment.u() + column];
    }

    private static void blit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v,
                             int width, int height) {
        //? if >=1.21.11 {
        /*guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 18, 9);
        *///?} else {
        guiGraphics.blit(texture, x, y, u, v, width, height, 18, 9);
        //?}
    }

    private static void blitTinted(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v,
                                   int width, int height, int color) {
        //? if >=1.21.11 {
        /*guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 18, 9,
                0xFF000000 | color);
        *///?} else {
        guiGraphics.flush();
        guiGraphics.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F,
                (color & 255) / 255.0F, 1.0F);
        blit(guiGraphics, texture, x, y, u, v, width, height);
        guiGraphics.flush();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //?}
    }

    private static void blitGlint(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int color) {
        //? if >=1.21.11 {
        /*
        guiGraphics.blit(GLINT_PIPELINE, ENCHANTED_GLINT_TEXTURE, x, y, u, v, width, 1,
                width * GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SIZE, GLINT_TEXTURE_SIZE, color);
        *///?} else {
        guiGraphics.blit(ENCHANTED_GLINT_TEXTURE, x, y, width, 1, (float) u, (float) v,
                width * GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SCALE, GLINT_TEXTURE_SIZE, GLINT_TEXTURE_SIZE);
        //?}
    }
}
