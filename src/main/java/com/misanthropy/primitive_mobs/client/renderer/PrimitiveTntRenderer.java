package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.entity.projectile.PrimitiveTnt;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionf;

public class PrimitiveTntRenderer extends EntityRenderer<PrimitiveTnt> {
    private static final Quaternionf TURN_BACK = com.mojang.math.Axis.YP.rotationDegrees(-90.0F);
    private static final Quaternionf TURN = com.mojang.math.Axis.YP.rotationDegrees(90.0F);

    private final BlockRenderDispatcher blockRenderer;

    public PrimitiveTntRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(PrimitiveTnt entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        int fuse = entity.getFuse();
        if ((float) fuse - partialTick + 1.0F < 10.0F) {
            float scale = 1.0F - ((float) fuse - partialTick + 1.0F) / 10.0F;
            scale = net.minecraft.util.Mth.clamp(scale, 0.0F, 1.0F);
            scale *= scale;
            scale *= scale;
            float bulge = 1.0F + scale * 0.3F;
            poseStack.scale(bulge, bulge, bulge);
        }
        poseStack.mulPose(TURN_BACK);
        poseStack.translate(-0.5D, -0.5D, 0.5D);
        poseStack.mulPose(TURN);
        TntMinecartRenderer.renderWhiteSolidBlock(blockRenderer, Blocks.TNT.defaultBlockState(), poseStack, buffer,
                packedLight, fuse / 5 % 2 == 0);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PrimitiveTnt entity) {
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }
}
