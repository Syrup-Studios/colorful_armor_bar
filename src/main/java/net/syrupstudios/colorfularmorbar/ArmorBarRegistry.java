package net.syrupstudios.colorfularmorbar;

import net.minecraft.core.registries.BuiltInRegistries;
//? if >=1.21.11 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.Equippable;
*///?} else {
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
//?}
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Version-neutral texture lookup and cache. Loader entrypoints own reload registration. */
public final class ArmorBarRegistry {
    //? if >=1.21.11 {
    /*public static final Identifier FALLBACK_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/armoricon/iron.png");
    private static final Map<String, Identifier> TEXTURE_CACHE = new HashMap<>();
    private static final Map<Identifier, boolean[]> ALPHA_MASK_CACHE = new HashMap<>();
    *///?} elif >=1.21 {
    public static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/armoricon/iron.png");
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, boolean[]> ALPHA_MASK_CACHE = new HashMap<>();
    //?} else {
    /*public static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/armoricon/iron.png");
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, boolean[]> ALPHA_MASK_CACHE = new HashMap<>();
    *///?}

    private static final int ICON_WIDTH = 18;
    private static final int ICON_HEIGHT = 9;
    private static final boolean[] FULL_ALPHA_MASK = createFullAlphaMask();

    private ArmorBarRegistry() {
    }

    //? if >=1.21.11 {
    /*public static Identifier getTexture(ItemStack stack, ResourceManager resourceManager) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        Identifier assetId = equippable == null
                ? null
                : equippable.assetId().map(key -> key.identifier()).orElse(null);
        String cacheKey = assetId == null ? itemId.toString() : assetId.toString();

        Identifier cached = TEXTURE_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Identifier texture = assetId == null ? null : findTexture(assetId, resourceManager);
        if (texture == null) {
            Identifier strippedItemId = Identifier.fromNamespaceAndPath(itemId.getNamespace(), stripEquipmentSuffix(itemId.getPath()));
            if (!strippedItemId.equals(assetId)) {
                texture = findTexture(strippedItemId, resourceManager);
            }
        }

        Identifier resolved = texture == null ? FALLBACK_TEXTURE : texture;
        TEXTURE_CACHE.put(cacheKey, resolved);
        return resolved;
    }

    private static Identifier findTexture(Identifier materialId, ResourceManager resourceManager) {
        Identifier texture = Identifier.fromNamespaceAndPath(
                materialId.getNamespace(), "textures/armoricon/" + materialId.getPath() + ".png");
        return resourceManager.getResource(texture).isPresent() ? texture : null;
    }
    *///?} else {
    public static ResourceLocation getTexture(ItemStack stack, ResourceManager resourceManager) {
        if (!(stack.getItem() instanceof ArmorItem armorItem)) {
            return FALLBACK_TEXTURE;
        }

        //? if >=1.21 {
        String materialName = armorItem.getMaterial().unwrapKey().map(key -> key.location().toString()).orElse("");
        if (materialName.isEmpty()) {
            ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(armorItem);
            materialName = itemKey.getNamespace() + ":" + stripEquipmentSuffix(itemKey.getPath());
        }
        //?} else {
        /*String materialName = armorItem.getMaterial().getName();
        *///?}

        String namespace = BuiltInRegistries.ITEM.getKey(armorItem).getNamespace();
        String pathName = materialName;
        if (materialName.contains(":")) {
            String[] parts = materialName.split(":", 2);
            namespace = parts[0];
            pathName = parts[1];
        }

        // Key on the resolved namespace so equal material names from different mods don't collide.
        String cacheKey = namespace + ":" + pathName;
        ResourceLocation cached = TEXTURE_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        //? if >=1.21 {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(namespace, "textures/armoricon/" + pathName + ".png");
        //?} else {
        /*ResourceLocation texture = new ResourceLocation(namespace, "textures/armoricon/" + pathName + ".png");
        *///?}
        ResourceLocation resolved = resourceManager.getResource(texture).isPresent() ? texture : FALLBACK_TEXTURE;
        TEXTURE_CACHE.put(cacheKey, resolved);
        return resolved;
    }
    //?}

    static String stripEquipmentSuffix(String path) {
        for (String suffix : new String[]{"_helmet", "_chestplate", "_leggings", "_boots"}) {
            if (path.endsWith(suffix)) {
                return path.substring(0, path.length() - suffix.length());
            }
        }
        return path;
    }

    //? if >=1.21.11 {
    /*public static boolean[] getAlphaMask(Identifier texture, ResourceManager resourceManager) {
    *///?} else {
    public static boolean[] getAlphaMask(ResourceLocation texture, ResourceManager resourceManager) {
    //?}
        boolean[] cached = ALPHA_MASK_CACHE.get(texture);
        if (cached != null) {
            return cached;
        }

        boolean[] mask = loadAlphaMask(texture, resourceManager);
        ALPHA_MASK_CACHE.put(texture, mask);
        return mask;
    }

    //? if >=1.21.11 {
    /*private static boolean[] loadAlphaMask(Identifier texture, ResourceManager resourceManager) {
    *///?} else {
    private static boolean[] loadAlphaMask(ResourceLocation texture, ResourceManager resourceManager) {
    //?}
        try {
            var resource = resourceManager.getResource(texture);
            if (resource.isEmpty()) {
                return FULL_ALPHA_MASK;
            }

            try (InputStream stream = resource.get().open()) {
                BufferedImage image = ImageIO.read(stream);
                if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                    return FULL_ALPHA_MASK;
                }

                boolean[] mask = new boolean[ICON_WIDTH * ICON_HEIGHT];
                for (int y = 0; y < ICON_HEIGHT; y++) {
                    int fromY = y * image.getHeight() / ICON_HEIGHT;
                    int toY = Math.max(fromY + 1, (y + 1) * image.getHeight() / ICON_HEIGHT);
                    for (int x = 0; x < ICON_WIDTH; x++) {
                        int fromX = x * image.getWidth() / ICON_WIDTH;
                        int toX = Math.max(fromX + 1, (x + 1) * image.getWidth() / ICON_WIDTH);
                        mask[y * ICON_WIDTH + x] = hasVisiblePixel(image, fromX, toX, fromY, toY);
                    }
                }
                return mask;
            }
        } catch (IOException exception) {
            ColorfulArmorBar.LOGGER.warn("Could not read armor icon {} for its enchantment glint mask", texture, exception);
            return FULL_ALPHA_MASK;
        }
    }

    private static boolean hasVisiblePixel(BufferedImage image, int fromX, int toX, int fromY, int toY) {
        int maxX = Math.min(toX, image.getWidth());
        int maxY = Math.min(toY, image.getHeight());
        for (int y = fromY; y < maxY; y++) {
            for (int x = fromX; x < maxX; x++) {
                if ((image.getRGB(x, y) >>> 24) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean[] createFullAlphaMask() {
        boolean[] mask = new boolean[ICON_WIDTH * ICON_HEIGHT];
        Arrays.fill(mask, true);
        return mask;
    }

    public static void clearCache() {
        TEXTURE_CACHE.clear();
        ALPHA_MASK_CACHE.clear();
    }
}
