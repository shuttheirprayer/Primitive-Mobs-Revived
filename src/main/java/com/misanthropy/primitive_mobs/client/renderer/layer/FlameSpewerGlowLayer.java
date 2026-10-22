package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.misanthropy.primitive_mobs.client.model.FlameSpewerModel;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.FlameSpewer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class FlameSpewerGlowLayer extends RenderLayer<FlameSpewer, FlameSpewerModel<FlameSpewer>> {
    private static final RenderType RENDER_TYPE =
            RenderType.entityCutoutNoCull(PrimitiveMobs.id("textures/entity/flamespewer/flamespewer_lava.png"));

    private final FlameSpewerModel<FlameSpewer> model;

    public FlameSpewerGlowLayer(RenderLayerParent<FlameSpewer, FlameSpewerModel<FlameSpewer>> parent,
                                EntityRendererProvider.Context context) {
        super(parent);
        this.model = new FlameSpewerModel<>(context.bakeLayer(FlameSpewerModel.LAYER));
        this.model.setTentaclesVisible(false);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, FlameSpewer entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        getParentModel().copyPropertiesTo(model);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
