package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.VoidEyeModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.VoidEyeSeenLayer;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.VoidEye;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VoidEyeRenderer extends MobRenderer<VoidEye, VoidEyeModel<VoidEye>> {
    private static final ResourceLocation TEXTURE = PrimitiveMobs.id("textures/entity/voideye/voideye.png");
    private static final RenderType BEAM = RenderType.entityCutoutNoCull(
            PrimitiveMobs.id("textures/entity/voideye/voideye_beam.png"));
    private static final RenderType WEAK_BEAM = RenderType.entityCutoutNoCull(
            PrimitiveMobs.id("textures/entity/voideye/voideye_beam_weak.png"));

    public VoidEyeRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidEyeModel<>(context.bakeLayer(VoidEyeModel.LAYER)), 0.5F);
        addLayer(new VoidEyeSeenLayer(this, context));
    }

    @Override
    public boolean shouldRender(VoidEye entity, Frustum frustum, double x, double y, double z) {
        if (super.shouldRender(entity, frustum, x, y, z)) {
            return true;
        }
        LivingEntity target = entity.getBeamTarget();
        if (target == null) {
            return false;
        }
        Vec3 targetPos = lerpPosition(target, target.getBbHeight() * 0.5D, 1.0F);
        Vec3 eyePos = lerpPosition(entity, entity.getEyeHeight(), 1.0F);
        return frustum.isVisible(new AABB(eyePos.x, eyePos.y, eyePos.z, targetPos.x, targetPos.y, targetPos.z));
    }

    private static Vec3 lerpPosition(LivingEntity entity, double yOffset, float partialTick) {
        return new Vec3(
                Mth.lerp(partialTick, entity.xOld, entity.getX()),
                Mth.lerp(partialTick, entity.yOld, entity.getY()) + yOffset,
                Mth.lerp(partialTick, entity.zOld, entity.getZ()));
    }

    @Override
    public void render(VoidEye entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        getModel().setBodyVisible(true);
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        LivingEntity target = entity.getBeamTarget();
        if (target != null) {
            renderBeam(entity, target, partialTick, poseStack, buffer);
        }
    }

    private void renderBeam(VoidEye entity, LivingEntity target, float partialTick, PoseStack poseStack,
                            MultiBufferSource buffer) {
        float progress = entity.getBeamProgress(partialTick);
        float time = progress * entity.getBeamDuration() + partialTick;
        float scroll = time * 0.5F % 1.0F;
        float eyeHeight = entity.getEyeHeight();

        poseStack.pushPose();
        poseStack.translate(0.0F, eyeHeight, 0.0F);
        Vec3 direction = lerpPosition(target, target.getBbHeight() * 0.5D, partialTick)
                .subtract(lerpPosition(entity, eyeHeight, partialTick));
        float length = (float) (direction.length() + 1.0D);
        direction = direction.normalize();
        float pitch = (float) Math.acos(direction.y);
        float yaw = (float) Math.atan2(direction.z, direction.x);
        poseStack.mulPose(Axis.YP.rotationDegrees((((float) Math.PI / 2F) - yaw) * (180F / (float) Math.PI)));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch * (180F / (float) Math.PI)));

        float spin = time * 0.05F * -1.5F;
        float intensity = progress * progress;
        int red = 64 + (int) (intensity * 191.0F);
        int green = 32 + (int) (intensity * 191.0F);
        int blue = 128 - (int) (intensity * 64.0F);

        float cosA = Mth.cos(spin + (float) Math.PI) * 0.2F;
        float sinA = Mth.sin(spin + (float) Math.PI) * 0.2F;
        float cosB = Mth.cos(spin) * 0.2F;
        float sinB = Mth.sin(spin) * 0.2F;
        float cosC = Mth.cos(spin + ((float) Math.PI / 2F)) * 0.2F;
        float sinC = Mth.sin(spin + ((float) Math.PI / 2F)) * 0.2F;
        float cosD = Mth.cos(spin + ((float) Math.PI * 1.5F)) * 0.2F;
        float sinD = Mth.sin(spin + ((float) Math.PI * 1.5F)) * 0.2F;

        float vStart = -1.0F + scroll;
        float vEnd = length * 2.5F + vStart;

        VertexConsumer consumer = buffer.getBuffer(entity.seesTarget() ? BEAM : WEAK_BEAM);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        vertex(consumer, matrix, normal, cosA, length, sinA, red, green, blue, 0.4999F, vEnd);
        vertex(consumer, matrix, normal, cosA, 0.0F, sinA, red, green, blue, 0.4999F, vStart);
        vertex(consumer, matrix, normal, cosB, 0.0F, sinB, red, green, blue, 0.0F, vStart);
        vertex(consumer, matrix, normal, cosB, length, sinB, red, green, blue, 0.0F, vEnd);
        vertex(consumer, matrix, normal, cosC, length, sinC, red, green, blue, 0.4999F, vEnd);
        vertex(consumer, matrix, normal, cosC, 0.0F, sinC, red, green, blue, 0.4999F, vStart);
        vertex(consumer, matrix, normal, cosD, 0.0F, sinD, red, green, blue, 0.0F, vStart);
        vertex(consumer, matrix, normal, cosD, length, sinD, red, green, blue, 0.0F, vEnd);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z,
                               int red, int green, int blue, float u, float v) {
        consumer.vertex(matrix, x, y, z).color(red, green, blue, 255).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(VoidEye entity) {
        return TEXTURE;
    }
}
