package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.renderer.layer.TintedEyesLayer;
import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.MotherSpider;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MotherSpiderRenderer extends MobRenderer<MotherSpider, SpiderModel<MotherSpider>> {
    private static final ResourceLocation TEXTURE = PrimitiveMobs.id("textures/entity/spiderfamily/motherspider.png");

    public MotherSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.8F);
        addLayer(new TintedEyesLayer<>(this, PrimitiveMobs.id("textures/entity/spider_eyes.png"), entity -> 0xFFFFFF));
    }

    @Override
    public ResourceLocation getTextureLocation(MotherSpider entity) {
        return TEXTURE;
    }
}
