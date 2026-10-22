package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.misanthropy.primitive_mobs.client.model.GroveSpriteModel;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.passive.GroveSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GroveSpriteStumpLayer extends RenderLayer<GroveSprite, GroveSpriteModel<GroveSprite>> {
    private static final RenderType RENDER_TYPE =
            RenderType.entityCutoutNoCull(PrimitiveMobs.id("textures/entity/grovesprite/grovestump.png"));

    private final GroveSpriteModel<GroveSprite> model;

    public GroveSpriteStumpLayer(RenderLayerParent<GroveSprite, GroveSpriteModel<GroveSprite>> parent,
                                 EntityRendererProvider.Context context) {
        super(parent);
        this.model = new GroveSpriteModel<>(context.bakeLayer(GroveSpriteModel.LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, GroveSprite entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        getParentModel().copyPropertiesTo(model);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        int color = entity.getLogColor();
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                ((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
    }
}
