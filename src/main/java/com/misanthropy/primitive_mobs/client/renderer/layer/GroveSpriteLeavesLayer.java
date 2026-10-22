package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.misanthropy.primitive_mobs.client.model.GroveSpriteLeavesModel;
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

public class GroveSpriteLeavesLayer extends RenderLayer<GroveSprite, GroveSpriteModel<GroveSprite>> {
    private static final RenderType LEAVES =
            RenderType.entityCutoutNoCull(PrimitiveMobs.id("textures/entity/grovesprite/groveleaf.png"));
    private static final RenderType CINDER_LEAVES =
            RenderType.entityCutoutNoCull(PrimitiveMobs.id("textures/entity/grovesprite/cinderleaf.png"));

    private final GroveSpriteLeavesModel<GroveSprite> model;

    public GroveSpriteLeavesLayer(RenderLayerParent<GroveSprite, GroveSpriteModel<GroveSprite>> parent,
                                  EntityRendererProvider.Context context) {
        super(parent);
        this.model = new GroveSpriteLeavesModel<>(context.bakeLayer(GroveSpriteLeavesModel.LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, GroveSprite entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        int color = entity.getLeavesColor();
        VertexConsumer consumer = buffer.getBuffer(entity.isCinder() ? CINDER_LEAVES : LEAVES);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                ((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
    }
}
