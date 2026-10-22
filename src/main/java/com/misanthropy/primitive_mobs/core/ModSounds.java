package com.misanthropy.primitive_mobs.core;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PrimitiveMobs.MODID);

    public static final RegistryObject<SoundEvent> BRAIN_SLIME_CHARGE = register("entity.brainslime.slimecharge");
    public static final RegistryObject<SoundEvent> MOTHER_SPIDER_SCREECH = register("entity.motherspider.spiderscreech");
    public static final RegistryObject<SoundEvent> GROVE_SPRITE_ANGRY = register("entity.grovesprite.angry");
    public static final RegistryObject<SoundEvent> GROVE_SPRITE_DEATH = register("entity.grovesprite.death");
    public static final RegistryObject<SoundEvent> GROVE_SPRITE_HURT = register("entity.grovesprite.hurt");
    public static final RegistryObject<SoundEvent> GROVE_SPRITE_IDLE = register("entity.grovesprite.idle");
    public static final RegistryObject<SoundEvent> GROVE_SPRITE_THANKS = register("entity.grovesprite.thanks");
    public static final RegistryObject<SoundEvent> GROVE_SPRITE_FRIEND = register("entity.grovesprite.friend");
    public static final RegistryObject<SoundEvent> TROLLAGER_IDLE = register("entity.trollager.idle");
    public static final RegistryObject<SoundEvent> TROLLAGER_HIT = register("entity.trollager.hit");
    public static final RegistryObject<SoundEvent> TROLLAGER_DEATH = register("entity.trollager.death");
    public static final RegistryObject<SoundEvent> TROLLAGER_ATTACK = register("entity.trollager.attack");
    public static final RegistryObject<SoundEvent> HARPY_IDLE = register("entity.harpy.idle");
    public static final RegistryObject<SoundEvent> HARPY_HURT = register("entity.harpy.hurt");
    public static final RegistryObject<SoundEvent> FLAME_SPEWER_IDLE = register("entity.flamespewer.idle");
    public static final RegistryObject<SoundEvent> VOID_EYE_IDLE = register("entity.voideye.idle");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(PrimitiveMobs.id(name)));
    }

    private ModSounds() {
    }
}
