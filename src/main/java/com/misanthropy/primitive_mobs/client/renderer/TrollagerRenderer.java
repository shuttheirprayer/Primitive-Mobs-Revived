package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.TrollagerModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.HeldItemLayer;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.Trollager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TrollagerRenderer extends MobRenderer<Trollager, TrollagerModel<Trollager>> {
    private static final ResourceLocation TROLL = PrimitiveMobs.id("textures/entity/troll/troll.png");
    private static final ResourceLocation STONED = PrimitiveMobs.id("textures/entity/troll/troll_stoned.png");

    public TrollagerRenderer(EntityRendererProvider.Context context) {
        super(context, new TrollagerModel<>(context.bakeLayer(TrollagerModel.LAYER)), 2.35F);
        addLayer(new HeldItemLayer<>(this, () -> getModel().blockHolder(), TrollagerRenderer::carriedBlock,
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.5F, 0.5F, 0.0F}, 1.5F));
    }

    private static ItemStack carriedBlock(Trollager trollager) {
        if (trollager.getAnimationState() != 1 && trollager.getAnimationState() != 2) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(trollager.getThrownBlockState().getBlock());
    }

    @Override
    protected void scale(Trollager entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Trollager entity) {
        return entity.isStone() ? STONED : TROLL;
    }
}
