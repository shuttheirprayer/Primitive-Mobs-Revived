package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.FlameSpewerModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.FlameSpewerGlowLayer;
import com.misanthropy.primitive_mobs.entity.monster.FlameSpewer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FlameSpewerRenderer extends SimpleMobRenderer<FlameSpewer, FlameSpewerModel<FlameSpewer>> {
    public FlameSpewerRenderer(EntityRendererProvider.Context context) {
        super(context, new FlameSpewerModel<>(context.bakeLayer(FlameSpewerModel.LAYER)), 0.5F,
                "flamespewer/flamespewer");
        addLayer(new FlameSpewerGlowLayer(this, context));
    }
}
