package net.syrupstudios.colorfularmorbar;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ArmorItem;

import java.util.HashMap;
import java.util.Map;

public class ArmorBarRegistry implements SimpleSynchronousResourceReloadListener {
    //? if >=1.21 {
    public static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/armoricon/iron.png");
    //?} else {
    /*public static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/armoricon/iron.png");
    *///?}
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    public static ResourceLocation getTexture(ArmorItem armorItem) {
        //? if >=1.21 {
        String materialName = armorItem.getMaterial().unwrapKey().map(key -> key.location().toString()).orElse("");
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

        if (Minecraft.getInstance().getResourceManager().getResource(targetTexture).isPresent()) {
            TEXTURE_CACHE.put(materialName, targetTexture);
            return targetTexture;
        }

        TEXTURE_CACHE.put(materialName, FALLBACK_TEXTURE);
        return FALLBACK_TEXTURE;
    }

    @Override
    public ResourceLocation getFabricId() {
        //? if >=1.21 {
        return ResourceLocation.fromNamespaceAndPath("colorful_armor_bar", "armor_bar_reload_listener");
        //?} else {
        /*return new ResourceLocation("colorful_armor_bar", "armor_bar_reload_listener");
        *///?}
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        TEXTURE_CACHE.clear();
    }
}