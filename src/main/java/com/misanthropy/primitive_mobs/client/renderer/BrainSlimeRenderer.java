package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.BrainSlime;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BrainSlimeRenderer extends MobRenderer<BrainSlime, SlimeModel<BrainSlime>> {
    private static final ResourceLocation TEXTURE = PrimitiveMobs.id("textures/entity/brainslime/slime_brain.png");

    public BrainSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        addLayer(new SlimeOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public void render(BrainSlime entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        shadowRadius = 0.25F * entity.getSize();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(BrainSlime entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float size = entity.getSize();
        float squish = Mth.lerp(partialTick, entity.oSquish, entity.squish) / (size * 0.5F + 1.0F);
        float stretch = 1.0F / (squish + 1.0F);
        poseStack.scale(stretch * size, 1.0F / stretch * size, stretch * size);
    }

    @Override
    public ResourceLocation getTextureLocation(BrainSlime entity) {
        return TEXTURE;
    }
}
