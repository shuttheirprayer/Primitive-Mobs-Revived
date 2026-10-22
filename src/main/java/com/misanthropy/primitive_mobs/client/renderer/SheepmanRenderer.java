package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.SheepmanModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.SheepmanWoolLayer;
import com.misanthropy.primitive_mobs.entity.passive.Sheepman;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SheepmanRenderer extends SimpleMobRenderer<Sheepman, SheepmanModel<Sheepman>> {
    public SheepmanRenderer(EntityRendererProvider.Context context) {
        super(context, new SheepmanModel<>(context.bakeLayer(SheepmanModel.LAYER)), 0.5F, "villager/sheepman");
        addLayer(new SheepmanWoolLayer(this, context));
    }

    @Override
    protected void scale(Sheepman entity, PoseStack poseStack, float partialTick) {
        getModel().showBody(entity.isSheared());
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
