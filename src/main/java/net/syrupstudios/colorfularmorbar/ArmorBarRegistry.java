package net.syrupstudios.colorfularmorbar;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.11 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.Equippable;
*///?} else {
import net.minecraft.world.item.ArmorItem;
//?}
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

import javax.imageio.ImageIO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Version-neutral texture lookup and cache. Loader entrypoints own reload registration. */
public final class ArmorBarRegistry {
    //? if >=1.21 {
    public static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/armoricon/iron.png");
    private static final ResourceLocation CUSTOM_TRIM_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "colorful_armor_bar", "textures/armoricon/trim.png");
    private static final ResourceLocation VANILLA_TRIM_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "minecraft", "textures/trims/items/chestplate_trim.png");
    private static final Map<String, TextureResult> TEXTURE_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, boolean[]> ALPHA_MASK_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, boolean[]> TRIM_ALPHA_MASK_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, Optional<Integer>> ITEM_COLOR_CACHE = new HashMap<>();
    //?} else {
    /*public static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/armoricon/iron.png");
    private static final ResourceLocation CUSTOM_TRIM_TEXTURE = new ResourceLocation(
            "colorful_armor_bar", "textures/armoricon/trim.png");
    private static final ResourceLocation VANILLA_TRIM_TEXTURE = new ResourceLocation(
            "minecraft", "textures/trims/items/chestplate_trim.png");
    private static final Map<String, TextureResult> TEXTURE_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, boolean[]> ALPHA_MASK_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, boolean[]> TRIM_ALPHA_MASK_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, Optional<Integer>> ITEM_COLOR_CACHE = new HashMap<>();
    *///?}

    private static final int ICON_WIDTH = 18;
    private static final int ICON_HEIGHT = 9;
    private static final int TRIM_ICON_SIZE = 9;
    private static final boolean[] FULL_ALPHA_MASK = createFullAlphaMask();
    private static final boolean[] EMPTY_TRIM_ALPHA_MASK = new boolean[TRIM_ICON_SIZE * TRIM_ICON_SIZE];

    private ArmorBarRegistry() {
    }

    public record TextureResult(ResourceLocation texture, boolean fallback) {
    }

    //? if >=1.21.11 {
    /*public static TextureResult getTexture(ItemStack stack, ResourceManager resourceManager) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        ResourceLocation assetId = equippable == null
                ? null
                : equippable.assetId().map(key -> key.identifier()).orElse(null);
        String cacheKey = (assetId == null ? "" : assetId.toString()) + "|" + itemId;

        TextureResult cached = TEXTURE_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        ResourceLocation texture = assetId == null ? null : findTexture(assetId, resourceManager);
        if (texture == null) {
            ResourceLocation strippedItemId = ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), stripEquipmentSuffix(itemId.getPath()));
            if (!strippedItemId.equals(assetId)) {
                texture = findTexture(strippedItemId, resourceManager);
            }
        }

        TextureResult resolved = new TextureResult(texture == null ? FALLBACK_TEXTURE : texture, texture == null);
        TEXTURE_CACHE.put(cacheKey, resolved);
        return resolved;
    }

    private static ResourceLocation findTexture(ResourceLocation materialId, ResourceManager resourceManager) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                materialId.getNamespace(), "textures/armoricon/" + materialId.getPath() + ".png");
        return resourceManager.getResource(texture).isPresent() ? texture : null;
    }
    *///?} else {
    public static TextureResult getTexture(ItemStack stack, ResourceManager resourceManager) {
        if (!(stack.getItem() instanceof ArmorItem armorItem)) {
            return new TextureResult(FALLBACK_TEXTURE, true);
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
        TextureResult cached = TEXTURE_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        //? if >=1.21 {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(namespace, "textures/armoricon/" + pathName + ".png");
        //?} else {
        /*ResourceLocation texture = new ResourceLocation(namespace, "textures/armoricon/" + pathName + ".png");
        *///?}
        boolean fallback = resourceManager.getResource(texture).isEmpty();
        TextureResult resolved = new TextureResult(fallback ? FALLBACK_TEXTURE : texture, fallback);
        TEXTURE_CACHE.put(cacheKey, resolved);
        return resolved;
    }
    //?}

    public static Integer getItemColor(ItemStack stack, ResourceManager resourceManager) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Optional<Integer> cached = ITEM_COLOR_CACHE.get(itemId);
        if (cached != null) {
            return cached.orElse(null);
        }
        Integer color = null;
        try {
            var modelTextures = modelTextures(itemId, resourceManager);
            if (!modelTextures.isEmpty()) {
                color = modeTextureLayers(modelTextures, resourceManager);
            }
        } catch (IOException | RuntimeException ignored) { }
        if (color == null) {
            try {
                color = modeTextureLayers(java.util.List.of(createLocation(
                        itemId.getNamespace(), "textures/item/" + itemId.getPath() + ".png")), resourceManager);
            } catch (IOException | RuntimeException ignored) { }
        }
        ITEM_COLOR_CACHE.put(itemId, Optional.ofNullable(color));
        return color;
    }

    private static java.util.List<ResourceLocation> modelTextures(ResourceLocation itemId, ResourceManager resourceManager)
            throws IOException {
        java.util.List<ResourceLocation> result = java.util.List.of();
        //? if >=1.21.11 {
        result = modernModelTextures(itemId, resourceManager);
        //?}
        if (result.isEmpty()) {
            JsonObject model = readJson(createLocation(
                    itemId.getNamespace(), "models/item/" + itemId.getPath() + ".json"), resourceManager);
            result = modelTextures(model);
        }
        return result;
    }

    private static java.util.List<ResourceLocation> modernModelTextures(ResourceLocation itemId,
                                                                        ResourceManager resourceManager)
            throws IOException {
        JsonObject item = readJson(createLocation(
                itemId.getNamespace(), "items/" + itemId.getPath() + ".json"), resourceManager);
        JsonObject modelNode = item != null && item.has("model") && item.get("model").isJsonObject()
                ? item.getAsJsonObject("model") : null;
        String modelId = modelNode != null && modelNode.has("model")
                ? modelNode.get("model").getAsString() : null;
        if (modelId == null) return java.util.List.of();
        try {
            ResourceLocation modelLocation = parseLocation(modelId);
            return modelTextures(readJson(createLocation(modelLocation.getNamespace(),
                    "models/" + modelLocation.getPath() + ".json"), resourceManager));
        } catch (RuntimeException ignored) {
            return java.util.List.of();
        }
    }

    private static java.util.List<ResourceLocation> modelTextures(JsonObject model) {
        if (model == null || !model.has("textures") || !model.get("textures").isJsonObject()) return java.util.List.of();
        JsonObject textures = model.getAsJsonObject("textures");
        java.util.List<ResourceLocation> result = new java.util.ArrayList<>();
        for (int layer = 0; layer < 3; layer++) {
            if (textures.has("layer" + layer) && textures.get("layer" + layer).isJsonPrimitive()) {
                String textureId = textures.get("layer" + layer).getAsString();
                if (!textureId.startsWith("#")) {
                    try {
                        ResourceLocation location = parseLocation(textureId);
                        result.add(createLocation(location.getNamespace(),
                                "textures/" + location.getPath() + ".png"));
                    } catch (RuntimeException ignored) { }
                }
            }
        }
        return result;
    }

    private static JsonObject readJson(ResourceLocation location, ResourceManager resourceManager) throws IOException {
        var resource = resourceManager.getResource(location);
        if (resource.isEmpty()) return null;
        try (InputStream stream = resource.get().open()) {
            JsonElement json = JsonParser.parseReader(new java.io.InputStreamReader(stream, StandardCharsets.UTF_8));
            return json.isJsonObject() ? json.getAsJsonObject() : null;
        }
    }

    private static ResourceLocation parseLocation(String value) {
        //? if >=1.21 {
        return ResourceLocation.parse(value);
        //?} else {
        /*return new ResourceLocation(value);
        *///?}
    }

    private static ResourceLocation createLocation(String namespace, String path) {
        //? if >=1.21 {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //?} else {
        /*return new ResourceLocation(namespace, path);
        *///?}
    }

    private static Integer modeTextureLayers(java.util.List<ResourceLocation> textures, ResourceManager resourceManager)
            throws IOException {
        Map<Integer, Long> colorWeights = new HashMap<>();
        int peak = 0;
        for (ResourceLocation texture : textures) {
            var resource = resourceManager.getResource(texture);
            if (resource.isEmpty()) continue;
            try (InputStream stream = resource.get().open()) {
                BufferedImage image = ImageIO.read(stream);
                if (image == null) continue;
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int pixel = image.getRGB(x, y);
                        int weight = pixel >>> 24;
                        if (weight > 0) {
                            colorWeights.merge(pixel & 0x00FFFFFF, (long) weight, Long::sum);
                            peak = Math.max(peak, Math.max(Math.max((pixel >>> 16 & 255) * weight / 255,
                                    (pixel >>> 8 & 255) * weight / 255), (pixel & 255) * weight / 255));
                        }
                    }
                }
            }
        }
        int mode = -1;
        long modeWeight = 0;
        for (var entry : colorWeights.entrySet()) {
            if (entry.getValue() > modeWeight || (entry.getValue() == modeWeight && entry.getKey() < mode)) {
                mode = entry.getKey();
                modeWeight = entry.getValue();
            }
        }
        if (mode < 0) return null;
        int modePeak = Math.max(mode >>> 16 & 255, Math.max(mode >>> 8 & 255, mode & 255));
        if (modePeak > 0) {
            peak = Math.max(peak, modePeak);
            int red = Math.min(255, (mode >>> 16 & 255) * peak / modePeak);
            int green = Math.min(255, (mode >>> 8 & 255) * peak / modePeak);
            int blue = Math.min(255, (mode & 255) * peak / modePeak);
            mode = red << 16 | green << 8 | blue;
        }
        return mode;
    }

    static String stripEquipmentSuffix(String path) {
        for (String suffix : new String[]{"_helmet", "_chestplate", "_leggings", "_boots"}) {
            if (path.endsWith(suffix)) {
                return path.substring(0, path.length() - suffix.length());
            }
        }
        return path;
    }

    public static boolean[] getAlphaMask(ResourceLocation texture, ResourceManager resourceManager) {
        boolean[] cached = ALPHA_MASK_CACHE.get(texture);
        if (cached != null) {
            return cached;
        }

        boolean[] mask = loadAlphaMask(texture, resourceManager, ICON_WIDTH, ICON_HEIGHT, FULL_ALPHA_MASK);
        ALPHA_MASK_CACHE.put(texture, mask);
        return mask;
    }

    public static boolean[] getTrimAlphaMask(ResourceLocation patternId, ResourceManager resourceManager) {
        //? if >=1.21 {
        ResourceLocation patternTexture = ResourceLocation.fromNamespaceAndPath(
                patternId.getNamespace(), "textures/armoricon/trims/" + patternId.getPath() + ".png");
        //?} else {
        /*ResourceLocation patternTexture = new ResourceLocation(
                patternId.getNamespace(), "textures/armoricon/trims/" + patternId.getPath() + ".png");
        *///?}
        boolean[] cached = TRIM_ALPHA_MASK_CACHE.get(patternTexture);
        if (cached != null) {
            return cached;
        }

        var texture = resourceManager.getResource(patternTexture).isPresent()
                ? patternTexture
                : resourceManager.getResource(CUSTOM_TRIM_TEXTURE).isPresent()
                        ? CUSTOM_TRIM_TEXTURE
                        : VANILLA_TRIM_TEXTURE;
        boolean[] mask = loadAlphaMask(
                texture, resourceManager, TRIM_ICON_SIZE, TRIM_ICON_SIZE, EMPTY_TRIM_ALPHA_MASK);
        TRIM_ALPHA_MASK_CACHE.put(patternTexture, mask);
        return mask;
    }

    private static boolean[] loadAlphaMask(ResourceLocation texture, ResourceManager resourceManager, int targetWidth,
                                           int targetHeight, boolean[] fallback) {
        try {
            var resource = resourceManager.getResource(texture);
            if (resource.isEmpty()) {
                return fallback;
            }

            try (InputStream stream = resource.get().open()) {
                BufferedImage image = ImageIO.read(stream);
                if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                    return fallback;
                }

                boolean[] mask = new boolean[targetWidth * targetHeight];
                for (int y = 0; y < targetHeight; y++) {
                    int fromY = y * image.getHeight() / targetHeight;
                    int toY = Math.max(fromY + 1, (y + 1) * image.getHeight() / targetHeight);
                    for (int x = 0; x < targetWidth; x++) {
                        int fromX = x * image.getWidth() / targetWidth;
                        int toX = Math.max(fromX + 1, (x + 1) * image.getWidth() / targetWidth);
                        mask[y * targetWidth + x] = hasVisiblePixel(image, fromX, toX, fromY, toY);
                    }
                }
                return mask;
            }
        } catch (IOException exception) {
            ColorfulArmorBar.LOGGER.warn("Could not read armor-bar mask texture {}", texture, exception);
            return fallback;
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
        TRIM_ALPHA_MASK_CACHE.clear();
        ITEM_COLOR_CACHE.clear();
    }
}
