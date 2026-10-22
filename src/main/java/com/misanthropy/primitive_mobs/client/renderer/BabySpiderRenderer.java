package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.renderer.layer.TintedEyesLayer;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.BabySpider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BabySpiderRenderer extends MobRenderer<BabySpider, SpiderModel<BabySpider>> {
    private static final ResourceLocation BABY = PrimitiveMobs.id("textures/entity/spiderfamily/babyspider.png");
    private static final ResourceLocation ADULT = PrimitiveMobs.id("textures/entity/spiderfamily/spider.png");

    public BabySpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.3F);
        addLayer(new TintedEyesLayer<>(this, PrimitiveMobs.id("textures/entity/spider_eyes.png"),
                entity -> entity.getEyeColor().getFireworkColor()));
    }

    @Override
    protected void scale(BabySpider entity, PoseStack poseStack, float partialTick) {
        float scale = 0.5F + entity.getGrowthLevel() * 0.1F;
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(BabySpider entity) {
        return entity.getGrowthLevel() >= BabySpider.MAX_GROWTH ? ADULT : BABY;
    }
}
