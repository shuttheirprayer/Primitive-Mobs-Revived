package com.misanthropy.primitive_mobs.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemColorSampler {
    private static final int DEFAULT_COLOR = 0xFFFFFF;
    private static final int OPAQUE_THRESHOLD = 128;

    private static final Reference2IntMap<Item> CACHE = new Reference2IntOpenHashMap<>();

    static {
        CACHE.defaultReturnValue(Integer.MIN_VALUE);
    }

    public static int averageColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return DEFAULT_COLOR;
        }
        Item item = stack.getItem();
        int cached = CACHE.getInt(item);
        if (cached != Integer.MIN_VALUE) {
            return cached;
        }
        int color = sample(stack);
        CACHE.put(item, color);
        return color;
    }

    private static int sample(ItemStack stack) {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
        TextureAtlasSprite sprite = model.getParticleIcon();
        NativeImage image = sprite.contents().getOriginalImage();
        long red = 0;
        long green = 0;
        long blue = 0;
        int samples = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int packed = image.getPixelRGBA(x, y);
                if (((packed >>> 24) & 0xFF) < OPAQUE_THRESHOLD) {
                    continue;
                }
                red += packed & 0xFF;
                green += (packed >> 8) & 0xFF;
                blue += (packed >> 16) & 0xFF;
                samples++;
            }
        }
        if (samples == 0) {
            return DEFAULT_COLOR;
        }
        return (int) (red / samples) << 16 | (int) (green / samples) << 8 | (int) (blue / samples);
    }

    private ItemColorSampler() {
    }
}
