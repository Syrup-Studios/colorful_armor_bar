package net.syrupstudios.colorfularmorbar;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ArmorItem;

import java.util.HashMap;
import java.util.Map;

/** Loader-neutral texture lookup and cache. Loader entrypoints own reload registration. */
public final class ArmorBarRegistry {
    //? if >=1.21 {
    public static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/armoricon/iron.png");
    //?} else {
    /*public static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/armoricon/iron.png");
     *///?}
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    private ArmorBarRegistry() {
    }

    public static ResourceLocation getTexture(ArmorItem armorItem, ResourceManager resourceManager) {
        //? if >=1.21 {
        // Use toString() instead of getPath() to keep the full "modid:material" identifier
        String materialName = armorItem.getMaterial().unwrapKey().map(key -> key.location().toString()).orElse("");

        // Fail-safe for inline/unregistered material holders: extract from the item name directly
        if (materialName.isEmpty()) {
            ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(armorItem);
            String cleanedPath = stripEquipmentSuffix(itemKey.getPath());
            materialName = itemKey.getNamespace() + ":" + cleanedPath;
        }
        //?} else {
        /*String materialName = armorItem.getMaterial().getName();
         *///?}

        if (TEXTURE_CACHE.containsKey(materialName)) {
            return TEXTURE_CACHE.get(materialName);
        }

        String namespace = "minecraft";
        String pathName = materialName;

        if (materialName.contains(":")) {
            String[] parts = materialName.split(":", 2);
            namespace = parts[0];
            pathName = parts[1];
        } else {
            namespace = BuiltInRegistries.ITEM.getKey(armorItem).getNamespace();
        }

        //? if >=1.21 {
        ResourceLocation targetTexture = ResourceLocation.fromNamespaceAndPath(namespace, "textures/armoricon/" + pathName + ".png");
        //?} else {
        /*ResourceLocation targetTexture = new ResourceLocation(namespace, "textures/armoricon/" + pathName + ".png");
         *///?}

        if (resourceManager.getResource(targetTexture).isPresent()) {
            TEXTURE_CACHE.put(materialName, targetTexture);
            return targetTexture;
        }

        TEXTURE_CACHE.put(materialName, FALLBACK_TEXTURE);
        return FALLBACK_TEXTURE;
    }

    private static String stripEquipmentSuffix(String path) {
        for (String suffix : new String[]{"_helmet", "_chestplate", "_leggings", "_boots"}) {
            if (path.endsWith(suffix)) {
                return path.substring(0, path.length() - suffix.length());
            }
        }
        return path;
    }

    public static void clearCache() {
        TEXTURE_CACHE.clear();
    }

}
