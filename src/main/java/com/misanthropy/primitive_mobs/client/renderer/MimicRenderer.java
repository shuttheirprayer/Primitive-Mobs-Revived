package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.MimicModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.MimicMouthLayer;
import com.misanthropy.primitive_mobs.entity.monster.Mimic;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MimicRenderer extends MobRenderer<Mimic, MimicModel<Mimic>> {
    private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/normal.png");

    public MimicRenderer(EntityRendererProvider.Context context) {
        super(context, new MimicModel<>(context.bakeLayer(MimicModel.LAYER)), 0.45F);
        addLayer(new MimicMouthLayer(this, context));
    }

    @Override
    protected void scale(Mimic entity, PoseStack poseStack, float partialTick) {
        getModel().setTeethVisible(false);
        getModel().setChestVisible(true);
        if (!entity.onGround()) {
            double motionY = entity.getDeltaMovement().y;
            float angle = motionY > 0.1D && motionY < 0.5D ? 25.0F : (float) (motionY * 70.0D);
            poseStack.mulPose(Axis.XN.rotationDegrees(angle));
        }
    }

    @Override
    protected float getBob(Mimic entity, float partialTick) {
        return (Mth.sin(entity.getMouthOpen(partialTick)) + 0.2F) * entity.getMouthSpeed(partialTick)
                - ((float) Math.PI / 4F);
    }

    @Override
    public ResourceLocation getTextureLocation(Mimic entity) {
        return CHEST_TEXTURE;
    }
}
