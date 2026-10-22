package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.renderer.layer.HeldItemLayer;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.SkeletonWarrior;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonWarriorRenderer extends HumanoidMobRenderer<SkeletonWarrior, SkeletonModel<SkeletonWarrior>> {
    private static final ResourceLocation TEXTURE = PrimitiveMobs.id("textures/entity/skeletonwarrior/skeletonwarrior.png");

    public SkeletonWarriorRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F);
        addLayer(new HumanoidArmorLayer<>(this,
                new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
                context.getModelManager()));
        addLayer(new HeldItemLayer<>(this, null, SkeletonWarrior::getSheathedWeapon,
                new float[]{0.0F, -90.0F, 0.0F}, new float[]{0.1F, 0.15F, 0.1F}, 1.0F));
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonWarrior entity) {
        return TEXTURE;
    }
}
