package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.TreasureSlime;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class TreasureSlimeGelLayer extends RenderLayer<TreasureSlime, SlimeModel<TreasureSlime>> {
    private static final RenderType RENDER_TYPE =
            RenderType.entityTranslucent(PrimitiveMobs.id("textures/entity/treasureslime/slime_treasure.png"));

    private final SlimeModel<TreasureSlime> model;

    public TreasureSlimeGelLayer(RenderLayerParent<TreasureSlime, SlimeModel<TreasureSlime>> parent,
                                 EntityRendererProvider.Context context) {
        super(parent);
        this.model = new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TreasureSlime entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        getParentModel().copyPropertiesTo(model);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        int color = entity.getDisplayColor();
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                ((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
    }
}
