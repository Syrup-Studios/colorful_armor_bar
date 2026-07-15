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

import java.util.HashMap;
import java.util.Map;

/** Version-neutral texture lookup and cache. Loader entrypoints own reload registration. */
public final class ArmorBarRegistry {
    //? if >=1.21.11 {
    /*public static final Identifier FALLBACK_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/armoricon/iron.png");
    private static final Map<String, Identifier> TEXTURE_CACHE = new HashMap<>();
    *///?} elif >=1.21 {
    public static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/armoricon/iron.png");
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    //?} else {
    /*public static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/armoricon/iron.png");
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    *///?}

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

        ResourceLocation cached = TEXTURE_CACHE.get(materialName);
        if (cached != null) {
            return cached;
        }

        String namespace = BuiltInRegistries.ITEM.getKey(armorItem).getNamespace();
        String pathName = materialName;
        if (materialName.contains(":")) {
            String[] parts = materialName.split(":", 2);
            namespace = parts[0];
            pathName = parts[1];
        }

        //? if >=1.21 {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(namespace, "textures/armoricon/" + pathName + ".png");
        //?} else {
        /*ResourceLocation texture = new ResourceLocation(namespace, "textures/armoricon/" + pathName + ".png");
        *///?}
        ResourceLocation resolved = resourceManager.getResource(texture).isPresent() ? texture : FALLBACK_TEXTURE;
        TEXTURE_CACHE.put(materialName, resolved);
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

    public static void clearCache() {
        TEXTURE_CACHE.clear();
    }
}
