package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.DodoModel;
import com.misanthropy.primitive_mobs.entity.passive.Dodo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

public class DodoRenderer extends SimpleMobRenderer<Dodo, DodoModel<Dodo>> {
    public DodoRenderer(EntityRendererProvider.Context context) {
        super(context, new DodoModel<>(context.bakeLayer(DodoModel.LAYER)), 0.4F, "rareanimals/dodo");
    }

    @Override
    protected float getBob(Dodo entity, float partialTick) {
        float flap = Mth.lerp(partialTick, entity.oFlap, entity.flap);
        float speed = Mth.lerp(partialTick, entity.oFlapSpeed, entity.flapSpeed);
        return (Mth.sin(flap) + 1.0F) * speed;
    }
}
