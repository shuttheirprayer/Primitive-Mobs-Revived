package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.FilchLizardModel;
import com.misanthropy.primitive_mobs.client.renderer.layer.HeldItemLayer;
import com.misanthropy.primitive_mobs.entity.passive.FilchLizard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

public class FilchLizardRenderer extends SimpleMobRenderer<FilchLizard, FilchLizardModel<FilchLizard>> {
    public FilchLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new FilchLizardModel<>(context.bakeLayer(FilchLizardModel.LAYER)), 0.4F, "filchlizard/filchlizard");
        addLayer(new HeldItemLayer<>(this, null, LivingEntity::getMainHandItem,
                new float[]{0.0F, 90.0F, 200.0F}, new float[]{-0.55F, -1.0F, -0.05F}, 1.0F));
    }
}
