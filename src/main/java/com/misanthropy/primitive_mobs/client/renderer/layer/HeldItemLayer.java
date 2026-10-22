package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class HeldItemLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final ItemRenderer itemRenderer;
    @Nullable
    private final Supplier<ModelPart> anchor;
    private final Function<T, ItemStack> item;
    private final Quaternionf rotationX;
    private final Quaternionf rotationY;
    private final Quaternionf rotationZ;
    private final float[] offset;
    private final float scale;

    public HeldItemLayer(RenderLayerParent<T, M> parent, @Nullable Supplier<ModelPart> anchor,
                         Function<T, ItemStack> item, float[] rotation, float[] offset, float scale) {
        super(parent);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.anchor = anchor;
        this.item = item;
        this.rotationX = Axis.XP.rotationDegrees(rotation[0]);
        this.rotationY = Axis.YP.rotationDegrees(rotation[1]);
        this.rotationZ = Axis.ZP.rotationDegrees(rotation[2]);
        this.offset = offset;
        this.scale = scale;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = item.apply(entity);
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        if (anchor != null) {
            anchor.get().translateAndRotate(poseStack);
        }
        poseStack.translate(offset[0], offset[1], offset[2]);
        poseStack.mulPose(rotationX);
        poseStack.mulPose(rotationY);
        poseStack.mulPose(rotationZ);
        if (scale != 1.0F) {
            poseStack.scale(scale, scale, scale);
        }
        itemRenderer.renderStatic(entity, stack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, poseStack, buffer,
                entity.level(), packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, entity.getId());
        poseStack.popPose();
    }
}
