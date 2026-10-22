package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.GroveSpriteModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.GroveSpriteLeavesLayer;
import com.misanthropy.primitive_mobs.client.renderer.layer.GroveSpriteStumpLayer;
import com.misanthropy.primitive_mobs.client.renderer.layer.HeldItemLayer;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.passive.GroveSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class GroveSpriteRenderer extends MobRenderer<GroveSprite, GroveSpriteModel<GroveSprite>> {
    private static final ResourceLocation BASE = PrimitiveMobs.id("textures/entity/grovesprite/grovebase.png");
    private static final ResourceLocation CINDER = PrimitiveMobs.id("textures/entity/grovesprite/grovecinder.png");

    public GroveSpriteRenderer(EntityRendererProvider.Context context) {
        super(context, new GroveSpriteModel<>(context.bakeLayer(GroveSpriteModel.LAYER)), 0.3F);
        addLayer(new GroveSpriteStumpLayer(this, context));
        addLayer(new GroveSpriteLeavesLayer(this, context));
        addLayer(new HeldItemLayer<>(this, () -> getModel().rightArm(), LivingEntity::getMainHandItem,
                new float[]{-90.0F, 180.0F, 0.0F}, new float[]{0.0375F, 0.125F, -0.275F}, 1.0F));
    }

    @Override
    protected void scale(GroveSprite entity, PoseStack poseStack, float partialTick) {
        poseStack.translate(0.0F, 0.9F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(GroveSprite entity) {
        return entity.isCinder() ? CINDER : BASE;
    }
}
