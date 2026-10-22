package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.YetiModel;
import com.misanthropy.primitive_mobs.entity.monster.Yeti;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class YetiRenderer extends SimpleMobRenderer<Yeti, YetiModel<Yeti>> {
    public YetiRenderer(EntityRendererProvider.Context context) {
        super(context, new YetiModel<>(context.bakeLayer(YetiModel.LAYER)), 0.9F, "yeti/yeti");
    }
}
