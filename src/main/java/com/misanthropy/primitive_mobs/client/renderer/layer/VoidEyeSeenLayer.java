package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.misanthropy.primitive_mobs.client.model.VoidEyeModel;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.VoidEye;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class VoidEyeSeenLayer extends RenderLayer<VoidEye, VoidEyeModel<VoidEye>> {
    private static final RenderType RENDER_TYPE = RenderType.eyes(PrimitiveMobs.id("textures/entity/voideye/voideye_seen.png"));

    private final VoidEyeModel<VoidEye> model;

    public VoidEyeSeenLayer(RenderLayerParent<VoidEye, VoidEyeModel<VoidEye>> parent,
                            EntityRendererProvider.Context context) {
        super(parent);
        this.model = new VoidEyeModel<>(context.bakeLayer(VoidEyeModel.LAYER));
        this.model.setBodyVisible(false);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, VoidEye entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasBeamTarget()) {
            return;
        }
        getParentModel().copyPropertiesTo(model);
        model.setBodyVisible(false);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);
        model.renderToBuffer(poseStack, consumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
