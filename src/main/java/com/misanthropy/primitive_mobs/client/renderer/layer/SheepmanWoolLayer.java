package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.misanthropy.primitive_mobs.client.model.SheepmanModel;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.passive.Sheepman;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.Sheep;

public class SheepmanWoolLayer extends RenderLayer<Sheepman, SheepmanModel<Sheepman>> {
    private static final RenderType RENDER_TYPE =
            RenderType.entityCutoutNoCull(PrimitiveMobs.id("textures/entity/villager/sheepman.png"));

    private final SheepmanModel<Sheepman> model;

    public SheepmanWoolLayer(RenderLayerParent<Sheepman, SheepmanModel<Sheepman>> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.model = new SheepmanModel<>(context.bakeLayer(SheepmanModel.LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Sheepman entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isSheared() || entity.isInvisible()) {
            return;
        }
        getParentModel().copyPropertiesTo(model);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float[] color = Sheep.getColorArray(entity.getFleeceColor());
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);

        model.showFoldedArms();
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                color[0], color[1], color[2], 1.0F);

        model.showWool();
        poseStack.pushPose();
        poseStack.scale(1.1F, 1.1F, 1.1F);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                color[0], color[1], color[2], 1.0F);
        poseStack.popPose();
    }
}
