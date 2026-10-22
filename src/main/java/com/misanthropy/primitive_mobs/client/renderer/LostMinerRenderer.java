package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.LostMinerModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.HeldItemLayer;
import com.misanthropy.primitive_mobs.entity.passive.LostMiner;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

public class LostMinerRenderer extends SimpleMobRenderer<LostMiner, LostMinerModel<LostMiner>> {
    public LostMinerRenderer(EntityRendererProvider.Context context) {
        super(context, new LostMinerModel<>(context.bakeLayer(LostMinerModel.LAYER)), 0.5F, "villager/lostminer");
        addLayer(new HeldItemLayer<>(this, () -> getModel().getCarryAnchor(false), LivingEntity::getMainHandItem,
                new float[]{90.0F, 180.0F, 0.0F}, new float[]{-0.25F, -0.5F, -0.1F}, 1.0F));
    }

    @Override
    protected void scale(LostMiner entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
