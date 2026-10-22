package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class SimpleMobRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    private final ResourceLocation texture;

    public SimpleMobRenderer(EntityRendererProvider.Context context, M model, float shadowRadius, String texturePath) {
        super(context, model, shadowRadius);
        this.texture = PrimitiveMobs.id("textures/entity/" + texturePath + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
