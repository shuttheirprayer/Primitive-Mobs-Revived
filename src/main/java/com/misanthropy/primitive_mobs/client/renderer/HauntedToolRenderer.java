package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.HauntedTool;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class HauntedToolRenderer extends EntityRenderer<HauntedTool> {
    private static final ResourceLocation TEXTURE = PrimitiveMobs.id("textures/entity/mimic/haunted_tool.png");
    private static final Quaternionf TILT = Axis.ZP.rotationDegrees(135.0F);

    public HauntedToolRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.2F;
    }

    @Override
    public void render(HauntedTool entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        ItemStack tool = entity.getMainHandItem();
        if (!tool.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.6D + Mth.sin(entity.getBob(partialTick)) * 0.1D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
            poseStack.mulPose(TILT);
            poseStack.scale(1.4F, 1.4F, 1.4F);
            Minecraft.getInstance().getItemRenderer().renderStatic(entity, tool, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                    false, poseStack, buffer, entity.level(), packedLight, OverlayTexture.NO_OVERLAY, entity.getId());
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HauntedTool entity) {
        return TEXTURE;
    }
}
