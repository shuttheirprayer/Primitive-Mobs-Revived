package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.LilyLurkerModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.HeldItemLayer;
import com.misanthropy.primitive_mobs.entity.monster.LilyLurker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

public class LilyLurkerRenderer extends SimpleMobRenderer<LilyLurker, LilyLurkerModel<LilyLurker>> {
    public LilyLurkerRenderer(EntityRendererProvider.Context context) {
        super(context, new LilyLurkerModel<>(context.bakeLayer(LilyLurkerModel.LAYER)), 0.5F, "lilylurker/lilylurker");
        addLayer(new HeldItemLayer<>(this, null, LivingEntity::getMainHandItem,
                new float[]{90.0F, 0.0F, 0.0F}, new float[]{-0.025F, -0.2F, -0.9F}, 2.0F));
    }
}
