package com.misanthropy.primitive_mobs.client.renderer;

import com.misanthropy.primitive_mobs.client.model.BlazingJuggernautModel;
import com.misanthropy.primitive_mobs.entity.monster.BlazingJuggernaut;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;

public class BlazingJuggernautRenderer extends SimpleMobRenderer<BlazingJuggernaut, BlazingJuggernautModel<BlazingJuggernaut>> {
    public BlazingJuggernautRenderer(EntityRendererProvider.Context context) {
        super(context, new BlazingJuggernautModel<>(context.bakeLayer(BlazingJuggernautModel.LAYER)), 0.5F,
                "blazingjuggernaut/blazingjuggernaut");
    }

    @Override
    protected int getBlockLightLevel(BlazingJuggernaut entity, BlockPos pos) {
        return 15;
    }
}
