package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.GoblinModel;
import com.misanthropy.primitive_mobs.entity.monster.Goblin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GoblinRenderer extends SimpleMobRenderer<Goblin, GoblinModel<Goblin>> {
    public GoblinRenderer(EntityRendererProvider.Context context) {
        super(context, new GoblinModel<>(context.bakeLayer(GoblinModel.LAYER)), 0.5F, "goblin/goblin");
    }
}
