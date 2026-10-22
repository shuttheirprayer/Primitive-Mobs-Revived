package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.entity.projectile.ThrownBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class ThrownBlockRenderer extends EntityRenderer<ThrownBlock> {
    private final BlockRenderDispatcher blockRenderer;

    public ThrownBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ThrownBlock entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        BlockState state = entity.getBlockState();
        if (state.getRenderShape() == RenderShape.MODEL) {
            poseStack.pushPose();
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            BlockPos pos = entity.blockPosition();
            blockRenderer.getModelRenderer().tesselateBlock(entity.level(), blockRenderer.getBlockModel(state), state,
                    pos, poseStack, buffer.getBuffer(net.minecraft.client.renderer.ItemBlockRenderTypes.getMovingBlockRenderType(state)),
                    false, entity.level().getRandom(), state.getSeed(pos), net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownBlock entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
