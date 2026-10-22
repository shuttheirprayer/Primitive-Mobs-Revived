package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.TravelingMerchantModel;
import com.misanthropy.primitive_mobs.entity.passive.TravelingMerchant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TravelingMerchantRenderer extends SimpleMobRenderer<TravelingMerchant, TravelingMerchantModel<TravelingMerchant>> {
    public TravelingMerchantRenderer(EntityRendererProvider.Context context) {
        super(context, new TravelingMerchantModel<>(context.bakeLayer(TravelingMerchantModel.LAYER)), 0.5F,
                "villager/travelingmerchant");
    }

    @Override
    protected void scale(TravelingMerchant entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
