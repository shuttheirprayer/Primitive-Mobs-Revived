package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.renderer.layer.PrimitiveCreeperPowerLayer;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.FestiveCreeper;
import com.misanthropy.primitive_mobs.entity.monster.PrimitiveCreeper;
import com.misanthropy.primitive_mobs.entity.monster.RocketCreeper;
import com.misanthropy.primitive_mobs.entity.monster.SupportCreeper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PrimitiveCreeperRenderer<T extends PrimitiveCreeper> extends MobRenderer<T, CreeperModel<T>> {
    private static final ResourceLocation ROCKET = PrimitiveMobs.id("textures/entity/rocketcreeper/rocketcreeper.png");
    private static final ResourceLocation FESTIVE = PrimitiveMobs.id("textures/entity/festivecreeper/festivecreeper.png");
    private static final ResourceLocation SUPPORT = PrimitiveMobs.id("textures/entity/supportcreeper/supportcreeper.png");

    public PrimitiveCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel<>(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        addLayer(new PrimitiveCreeperPowerLayer<>(this, context.getModelSet()));
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        float swell = entity.getSwelling(partialTick);
        float wobble = 1.0F + Mth.sin(swell * 100.0F) * swell * 0.01F;
        swell = Mth.clamp(swell, 0.0F, 1.0F);
        swell *= swell;
        swell *= swell;
        float horizontal = (1.0F + swell * 0.4F) * wobble;
        float vertical = (1.0F + swell * 0.1F) / wobble;
        poseStack.scale(horizontal, vertical, horizontal);

        if (entity instanceof RocketCreeper rocket && !rocket.onGround() && rocket.isLaunched()) {
            double motionY = rocket.getDeltaMovement().y;
            float angle = motionY > 0.1D && motionY < 0.5D ? 25.0F : (float) (motionY * 175.0D);
            poseStack.mulPose(Axis.XN.rotationDegrees(angle));
        }
    }

    @Override
    protected float getWhiteOverlayProgress(T entity, float partialTick) {
        float swell = entity.getSwelling(partialTick);
        return (int) (swell * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swell, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof FestiveCreeper) {
            return FESTIVE;
        }
        if (entity instanceof SupportCreeper) {
            return SUPPORT;
        }
        return ROCKET;
    }
}
