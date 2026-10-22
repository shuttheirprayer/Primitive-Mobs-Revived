package com.misanthropy.primitive_mobs.core;

import com.misanthropy.primitive_mobs.entity.monster.*;
import com.misanthropy.primitive_mobs.entity.passive.*;
import com.misanthropy.primitive_mobs.entity.projectile.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PrimitiveMobs.MODID);

    public static final RegistryObject<EntityType<Chameleon>> CHAMELEON =
            mob("chameleon", Chameleon::new, MobCategory.CREATURE, 0.7F, 0.5F);
    public static final RegistryObject<EntityType<TreasureSlime>> TREASURE_SLIME =
            mob("treasure_slime", TreasureSlime::new, MobCategory.MONSTER, 2.04F, 2.04F);
    public static final RegistryObject<EntityType<HauntedTool>> HAUNTED_TOOL =
            mob("haunted_tool", HauntedTool::new, MobCategory.MONSTER, 0.6F, 1.0F);
    public static final RegistryObject<EntityType<GroveSprite>> GROVE_SPRITE =
            mob("grovesprite", GroveSprite::new, MobCategory.CREATURE, 0.5F, 1.0F);
    public static final RegistryObject<EntityType<BewitchedTome>> BEWITCHED_TOME =
            mob("bewitched_tome", BewitchedTome::new, MobCategory.MONSTER, 0.5F, 1.0F);
    public static final RegistryObject<EntityType<FilchLizard>> FILCH_LIZARD =
            mob("filch_lizard", FilchLizard::new, MobCategory.CREATURE, 0.6F, 0.5F);
    public static final RegistryObject<EntityType<BrainSlime>> BRAIN_SLIME =
            mob("brain_slime", BrainSlime::new, MobCategory.MONSTER, 2.04F, 2.04F);
    public static final RegistryObject<EntityType<RocketCreeper>> ROCKET_CREEPER =
            mob("rocket_creeper", RocketCreeper::new, MobCategory.MONSTER, 0.6F, 1.7F);
    public static final RegistryObject<EntityType<FestiveCreeper>> FESTIVE_CREEPER =
            mob("festive_creeper", FestiveCreeper::new, MobCategory.MONSTER, 0.6F, 1.7F);
    public static final RegistryObject<EntityType<SupportCreeper>> SUPPORT_CREEPER =
            mob("support_creeper", SupportCreeper::new, MobCategory.MONSTER, 0.6F, 1.7F);
    public static final RegistryObject<EntityType<SkeletonWarrior>> SKELETON_WARRIOR =
            mob("skeleton_warrior", SkeletonWarrior::new, MobCategory.MONSTER, 0.6F, 1.99F);
    public static final RegistryObject<EntityType<BlazingJuggernaut>> BLAZING_JUGGERNAUT =
            mob("blazing_juggernaut", BlazingJuggernaut::new, MobCategory.MONSTER, 0.9F, 2.4F);
    public static final RegistryObject<EntityType<LilyLurker>> LILY_LURKER =
            mob("lily_lurker", LilyLurker::new, MobCategory.MONSTER, 0.5F, 0.98F);
    public static final RegistryObject<EntityType<MotherSpider>> MOTHER_SPIDER =
            mob("mother_spider", MotherSpider::new, MobCategory.MONSTER, 1.4F, 0.9F);
    public static final RegistryObject<EntityType<BabySpider>> BABY_SPIDER =
            mob("baby_spider", BabySpider::new, MobCategory.MONSTER, 0.5F, 0.3F);
    public static final RegistryObject<EntityType<Trollager>> TROLLAGER =
            mob("trollager", Trollager::new, MobCategory.MONSTER, 2.25F, 3.0F);
    public static final RegistryObject<EntityType<LostMiner>> LOST_MINER =
            mob("lost_miner", LostMiner::new, MobCategory.CREATURE, 0.6F, 1.95F);
    public static final RegistryObject<EntityType<TravelingMerchant>> TRAVELING_MERCHANT =
            mob("traveling_merchant", TravelingMerchant::new, MobCategory.CREATURE, 0.6F, 1.95F);
    public static final RegistryObject<EntityType<Dodo>> DODO =
            mob("dodo", Dodo::new, MobCategory.CREATURE, 0.75F, 0.75F);
    public static final RegistryObject<EntityType<Mimic>> MIMIC =
            mob("mimic", Mimic::new, MobCategory.MONSTER, 0.9F, 0.9F);
    public static final RegistryObject<EntityType<Sheepman>> SHEEPMAN =
            mob("sheepman", Sheepman::new, MobCategory.CREATURE, 0.6F, 1.95F);
    public static final RegistryObject<EntityType<Goblin>> GOBLIN =
            mob("goblin", Goblin::new, MobCategory.MONSTER, 0.6F, 1.6F);
    public static final RegistryObject<EntityType<Harpy>> HARPY =
            mob("harpy", Harpy::new, MobCategory.MONSTER, 0.8F, 1.0F);
    public static final RegistryObject<EntityType<FlameSpewer>> FLAME_SPEWER =
            mob("flame_spewer", FlameSpewer::new, MobCategory.MONSTER, 1.0F, 1.25F);
    public static final RegistryObject<EntityType<VoidEye>> VOID_EYE =
            mob("void_eye", VoidEye::new, MobCategory.MONSTER, 0.6F, 0.6F);
    public static final RegistryObject<EntityType<Yeti>> YETI =
            mob("yeti", Yeti::new, MobCategory.MONSTER, 1.7F, 3.0F);

    public static final RegistryObject<EntityType<PrimitiveTnt>> PRIMITIVE_TNT = ENTITIES.register("primitive_tnt_primed",
            () -> EntityType.Builder.<PrimitiveTnt>of(PrimitiveTnt::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).fireImmune().clientTrackingRange(10).updateInterval(10)
                    .build("primitive_tnt_primed"));
    public static final RegistryObject<EntityType<FlameSpit>> FLAME_SPIT = ENTITIES.register("flame_spit",
            () -> EntityType.Builder.<FlameSpit>of(FlameSpit::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F).fireImmune().clientTrackingRange(4).updateInterval(10)
                    .build("flame_spit"));
    public static final RegistryObject<EntityType<ThrownBlock>> THROWN_BLOCK = ENTITIES.register("thrown_block",
            () -> EntityType.Builder.<ThrownBlock>of(ThrownBlock::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10)
                    .build("thrown_block"));
    public static final RegistryObject<EntityType<ThrownPrimitiveEgg>> PRIMITIVE_EGG = ENTITIES.register("primitive_egg",
            () -> EntityType.Builder.<ThrownPrimitiveEgg>of(ThrownPrimitiveEgg::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                    .build("primitive_egg"));
    public static final RegistryObject<EntityType<ThrownSpiderEgg>> SPIDER_EGG_PROJECTILE = ENTITIES.register("spider_egg",
            () -> EntityType.Builder.<ThrownSpiderEgg>of(ThrownSpiderEgg::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                    .build("spider_egg"));

    private static <T extends Entity> RegistryObject<EntityType<T>> mob(String name, EntityType.EntityFactory<T> factory,
                                                                       MobCategory category, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, category)
                .sized(width, height).clientTrackingRange(10).build(name));
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(CHAMELEON.get(), Chameleon.createAttributes().build());
        event.put(TREASURE_SLIME.get(), TreasureSlime.createAttributes().build());
        event.put(HAUNTED_TOOL.get(), HauntedTool.createAttributes().build());
        event.put(GROVE_SPRITE.get(), GroveSprite.createAttributes().build());
        event.put(BEWITCHED_TOME.get(), BewitchedTome.createAttributes().build());
        event.put(FILCH_LIZARD.get(), FilchLizard.createAttributes().build());
        event.put(BRAIN_SLIME.get(), BrainSlime.createAttributes().build());
        event.put(ROCKET_CREEPER.get(), RocketCreeper.createAttributes().build());
        event.put(FESTIVE_CREEPER.get(), FestiveCreeper.createAttributes().build());
        event.put(SUPPORT_CREEPER.get(), SupportCreeper.createAttributes().build());
        event.put(SKELETON_WARRIOR.get(), SkeletonWarrior.createAttributes().build());
        event.put(BLAZING_JUGGERNAUT.get(), BlazingJuggernaut.createAttributes().build());
        event.put(LILY_LURKER.get(), LilyLurker.createAttributes().build());
        event.put(MOTHER_SPIDER.get(), MotherSpider.createAttributes().build());
        event.put(BABY_SPIDER.get(), BabySpider.createAttributes().build());
        event.put(TROLLAGER.get(), Trollager.createAttributes().build());
        event.put(LOST_MINER.get(), LostMiner.createAttributes().build());
        event.put(TRAVELING_MERCHANT.get(), TravelingMerchant.createAttributes().build());
        event.put(DODO.get(), Dodo.createAttributes().build());
        event.put(MIMIC.get(), Mimic.createAttributes().build());
        event.put(SHEEPMAN.get(), Sheepman.createAttributes().build());
        event.put(GOBLIN.get(), Goblin.createAttributes().build());
        event.put(HARPY.get(), Harpy.createAttributes().build());
        event.put(FLAME_SPEWER.get(), FlameSpewer.createAttributes().build());
        event.put(VOID_EYE.get(), VoidEye.createAttributes().build());
        event.put(YETI.get(), Yeti.createAttributes().build());
    }

    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(CHAMELEON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Chameleon::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(FILCH_LIZARD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FilchLizard::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(GROVE_SPRITE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GroveSprite::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(DODO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Dodo::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(LOST_MINER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LostMiner::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(TRAVELING_MERCHANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TravelingMerchant::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(SHEEPMAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Sheepman::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

        registerMonster(event, TREASURE_SLIME.get());
        registerMonster(event, HAUNTED_TOOL.get());
        event.register(BEWITCHED_TOME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BewitchedTome::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(BRAIN_SLIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BrainSlime::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ROCKET_CREEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RocketCreeper::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        registerMonster(event, FESTIVE_CREEPER.get());
        registerMonster(event, SUPPORT_CREEPER.get());
        registerMonster(event, SKELETON_WARRIOR.get());
        registerMonster(event, BLAZING_JUGGERNAUT.get());
        registerMonster(event, MOTHER_SPIDER.get());
        registerMonster(event, BABY_SPIDER.get());
        event.register(TROLLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Trollager::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        registerMonster(event, MIMIC.get());
        event.register(GOBLIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Goblin::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(VOID_EYE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VoidEye::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(HARPY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Harpy::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(LILY_LURKER.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LilyLurker::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(FLAME_SPEWER.get(), SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FlameSpewer::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(YETI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private static <T extends Mob> void registerMonster(SpawnPlacementRegisterEvent event, EntityType<T> type) {
        event.register(type, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,

                (mobType, level, spawnType, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL
                        && (level.getLevel().dimensionType().ultraWarm() || Monster.isDarkEnoughToSpawn(level, pos, random))
                        && Mob.checkMobSpawnRules(mobType, level, spawnType, pos, random),
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private ModEntities() {
    }
}
