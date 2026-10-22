package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.ChameleonModel;
import com.misanthropy.primitive_mobs.entity.passive.Chameleon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ChameleonRenderer extends SimpleMobRenderer<Chameleon, ChameleonModel<Chameleon>> {
    public ChameleonRenderer(EntityRendererProvider.Context context) {
        super(context, new ChameleonModel<>(context.bakeLayer(ChameleonModel.LAYER)), 0.4F, "chameleon/chameleon");
    }
}
