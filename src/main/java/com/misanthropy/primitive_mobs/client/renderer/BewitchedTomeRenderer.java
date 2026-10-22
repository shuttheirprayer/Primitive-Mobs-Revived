package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.EnchantedBookModel;
import com.misanthropy.primitive_mobs.entity.monster.BewitchedTome;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class BewitchedTomeRenderer extends SimpleMobRenderer<BewitchedTome, EnchantedBookModel<BewitchedTome>> {
    private static final Quaternionf TILT = Axis.XP.rotationDegrees(-30.0F);

    public BewitchedTomeRenderer(EntityRendererProvider.Context context) {
        super(context, new EnchantedBookModel<>(context.bakeLayer(EnchantedBookModel.LAYER)), 0.4F, "enchantedbook/book");
    }

    @Override
    protected void scale(BewitchedTome entity, PoseStack poseStack, float partialTick) {
        float lift = (Mth.sin(entity.getBob(partialTick)) + 0.5F) * entity.getBobSpeed(partialTick) * 1.5F;
        poseStack.translate(0.0F, lift - 0.8F, 0.0F);
        poseStack.mulPose(TILT);
    }

    @Override
    protected float getBob(BewitchedTome entity, float partialTick) {
        return (Mth.sin(entity.getBob(partialTick)) + 0.2F) * entity.getBobSpeed(partialTick);
    }
}
