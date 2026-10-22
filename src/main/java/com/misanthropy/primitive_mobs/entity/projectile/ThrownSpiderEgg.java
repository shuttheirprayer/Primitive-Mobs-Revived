package com.misanthropy.primitive_mobs.entity.projectile;

import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.core.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownSpiderEgg extends ThrownPrimitiveEgg {
    public ThrownSpiderEgg(EntityType<? extends ThrownSpiderEgg> type, Level level) {
        super(type, level);
    }

    public ThrownSpiderEgg(Level level, LivingEntity thrower, int spawnChance) {
        super(ModEntities.SPIDER_EGG_PROJECTILE.get(), level, thrower, Kind.BABY_SPIDER, spawnChance);
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide) {
            ItemEntity shell = new ItemEntity(level(), getX(), getY() + 0.5D, getZ(),
                    new ItemStack(ModItems.SPIDER_EGGSHELL.get()));
            shell.setDefaultPickUpDelay();
            level().addFreshEntity(shell);
        }
        super.onHit(result);
    }
}
