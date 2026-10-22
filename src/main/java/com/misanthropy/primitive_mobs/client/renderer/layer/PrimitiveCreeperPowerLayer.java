package com.misanthropy.primitive_mobs.client.renderer.layer;

import com.misanthropy.primitive_mobs.entity.monster.PrimitiveCreeper;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

public class PrimitiveCreeperPowerLayer<T extends PrimitiveCreeper> extends EnergySwirlLayer<T, CreeperModel<T>> {
    private static final ResourceLocation POWER_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    private final CreeperModel<T> model;

    public PrimitiveCreeperPowerLayer(RenderLayerParent<T, CreeperModel<T>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new CreeperModel<>(modelSet.bakeLayer(ModelLayers.CREEPER_ARMOR));
    }

    @Override
    protected float xOffset(float tickCount) {
        return tickCount * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return POWER_TEXTURE;
    }

    @Override
    protected EntityModel<T> model() {
        return model;
    }
}
