package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.HarpyModel;
import com.misanthropy.primitive_mobs.entity.monster.Harpy;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HarpyRenderer extends SimpleMobRenderer<Harpy, HarpyModel<Harpy>> {
    public HarpyRenderer(EntityRendererProvider.Context context) {
        super(context, new HarpyModel<>(context.bakeLayer(HarpyModel.LAYER)), 0.5F, "harpy/harpy");
    }
}
