package com.misanthropy.primitive_mobs.core;

import com.misanthropy.primitive_mobs.entity.projectile.ThrownPrimitiveEgg;
import com.misanthropy.primitive_mobs.item.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PrimitiveMobs.MODID);

    private static final List<RegistryObject<? extends Item>> TAB_ORDER = new ArrayList<>();

    public static final FoodProperties RAW_DODO_FOOD = new FoodProperties.Builder()
            .nutrition(4).saturationMod(1.2F).meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.3F).build();
    public static final FoodProperties COOKED_DODO_FOOD = new FoodProperties.Builder()
            .nutrition(8).saturationMod(2.4F).meat().build();

    public static final RegistryObject<Item> CAMOUFLAGE_DYE = item("camouflage_dye",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<CamouflageArmorItem> CAMOUFLAGE_HELMET = item("camouflage_helmet",
            () -> new CamouflageArmorItem(ArmorItem.Type.HELMET));
    public static final RegistryObject<CamouflageArmorItem> CAMOUFLAGE_CHESTPLATE = item("camouflage_chestplate",
            () -> new CamouflageArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<CamouflageArmorItem> CAMOUFLAGE_LEGGINGS = item("camouflage_leggings",
            () -> new CamouflageArmorItem(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<CamouflageArmorItem> CAMOUFLAGE_BOOTS = item("camouflage_boots",
            () -> new CamouflageArmorItem(ArmorItem.Type.BOOTS));
    public static final RegistryObject<Item> RAW_DODO = item("dodo",
            () -> new Item(new Item.Properties().food(RAW_DODO_FOOD)));
    public static final RegistryObject<Item> COOKED_DODO = item("cooked_dodo",
            () -> new Item(new Item.Properties().food(COOKED_DODO_FOOD)));
    public static final RegistryObject<Item> DODO_EGG = item("dodo_egg",
            () -> new ThrownEggItem(ThrownPrimitiveEgg.Kind.DODO, 8, false));
    public static final RegistryObject<Item> SPIDER_EGG = item("spider_egg",
            () -> new ThrownEggItem(ThrownPrimitiveEgg.Kind.BABY_SPIDER, 1, false));
    public static final RegistryObject<Item> MYSTERY_EGG_FESTIVE = item("mysteryegg1",
            () -> new ThrownEggItem(ThrownPrimitiveEgg.Kind.FESTIVE_CREEPER, 1, true));
    public static final RegistryObject<Item> MYSTERY_EGG_SUPPORT = item("mysteryegg2",
            () -> new ThrownEggItem(ThrownPrimitiveEgg.Kind.SUPPORT_CREEPER, 1, true));
    public static final RegistryObject<Item> MYSTERY_EGG_ROCKET = item("mysteryegg3",
            () -> new ThrownEggItem(ThrownPrimitiveEgg.Kind.ROCKET_CREEPER, 1, true));
    public static final RegistryObject<Item> SPIDER_EGGSHELL = item("spider_eggshell", SpiderEggshellItem::new);
    public static final RegistryObject<Item> MIMIC_ORB = item("mimic_orb", MimicOrbItem::new);
    public static final RegistryObject<Item> GOBLIN_MACE = item("goblin_mace", GoblinMaceItem::new);
    public static final RegistryObject<Item> WONDER_SAP = item("wonder_sap", WonderSapItem::new);

    public static final RegistryObject<ForgeSpawnEggItem> CHAMELEON_SPAWN_EGG =
            spawnEgg("chameleon", ModEntities.CHAMELEON, 0x048C00, 0x025600);
    public static final RegistryObject<ForgeSpawnEggItem> TREASURE_SLIME_SPAWN_EGG =
            spawnEgg("treasure_slime", ModEntities.TREASURE_SLIME, 0xFFF299, 0xFFE7B2);
    public static final RegistryObject<ForgeSpawnEggItem> HAUNTED_TOOL_SPAWN_EGG =
            spawnEgg("haunted_tool", ModEntities.HAUNTED_TOOL, 0x493955, 0x68519E);
    public static final RegistryObject<ForgeSpawnEggItem> GROVE_SPRITE_SPAWN_EGG =
            spawnEgg("grovesprite", ModEntities.GROVE_SPRITE, 0x5B4A7D, 0x62A32F);
    public static final RegistryObject<ForgeSpawnEggItem> BEWITCHED_TOME_SPAWN_EGG =
            spawnEgg("bewitched_tome", ModEntities.BEWITCHED_TOME, 0xB76035, 0xD0CBD0);
    public static final RegistryObject<ForgeSpawnEggItem> FILCH_LIZARD_SPAWN_EGG =
            spawnEgg("filch_lizard", ModEntities.FILCH_LIZARD, 0xC2B694, 0xD1C400);
    public static final RegistryObject<ForgeSpawnEggItem> BRAIN_SLIME_SPAWN_EGG =
            spawnEgg("brain_slime", ModEntities.BRAIN_SLIME, 0xC69970, 0xD1A2BD);
    public static final RegistryObject<ForgeSpawnEggItem> ROCKET_CREEPER_SPAWN_EGG =
            spawnEgg("rocket_creeper", ModEntities.ROCKET_CREEPER, 0x4CAF50, 0x000000);
    public static final RegistryObject<ForgeSpawnEggItem> FESTIVE_CREEPER_SPAWN_EGG =
            spawnEgg("festive_creeper", ModEntities.FESTIVE_CREEPER, 0xBC3D08, 0x000000);
    public static final RegistryObject<ForgeSpawnEggItem> SUPPORT_CREEPER_SPAWN_EGG =
            spawnEgg("support_creeper", ModEntities.SUPPORT_CREEPER, 0xDBC72F, 0x000000);
    public static final RegistryObject<ForgeSpawnEggItem> SKELETON_WARRIOR_SPAWN_EGG =
            spawnEgg("skeleton_warrior", ModEntities.SKELETON_WARRIOR, 0xABAAC8, 0x6C4F39);
    public static final RegistryObject<ForgeSpawnEggItem> BLAZING_JUGGERNAUT_SPAWN_EGG =
            spawnEgg("blazing_juggernaut", ModEntities.BLAZING_JUGGERNAUT, 0x30179C, 0xB0A238);
    public static final RegistryObject<ForgeSpawnEggItem> LILY_LURKER_SPAWN_EGG =
            spawnEgg("lily_lurker", ModEntities.LILY_LURKER, 0x593F29, 0x3D3A1C);
    public static final RegistryObject<ForgeSpawnEggItem> MOTHER_SPIDER_SPAWN_EGG =
            spawnEgg("mother_spider", ModEntities.MOTHER_SPIDER, 0x250FE2, 0xA80B0E);
    public static final RegistryObject<ForgeSpawnEggItem> BABY_SPIDER_SPAWN_EGG =
            spawnEgg("baby_spider", ModEntities.BABY_SPIDER, 0xB59D28, 0xA80B0E);
    public static final RegistryObject<ForgeSpawnEggItem> TROLLAGER_SPAWN_EGG =
            spawnEgg("trollager", ModEntities.TROLLAGER, 0x567A1D, 0x35275F);
    public static final RegistryObject<ForgeSpawnEggItem> LOST_MINER_SPAWN_EGG =
            spawnEgg("lost_miner", ModEntities.LOST_MINER, 0x6C3F66, 0xBD9832);
    public static final RegistryObject<ForgeSpawnEggItem> TRAVELING_MERCHANT_SPAWN_EGG =
            spawnEgg("traveling_merchant", ModEntities.TRAVELING_MERCHANT, 0x606FD1, 0xBD9832);
    public static final RegistryObject<ForgeSpawnEggItem> DODO_SPAWN_EGG =
            spawnEgg("dodo", ModEntities.DODO, 0x725C43, 0xBCA6CC);
    public static final RegistryObject<ForgeSpawnEggItem> MIMIC_SPAWN_EGG =
            spawnEgg("mimic", ModEntities.MIMIC, 0xAB752D, 0x2A231D);
    public static final RegistryObject<ForgeSpawnEggItem> SHEEPMAN_SPAWN_EGG =
            spawnEgg("sheepman", ModEntities.SHEEPMAN, 0x2621E6, 0xB39140);
    public static final RegistryObject<ForgeSpawnEggItem> GOBLIN_SPAWN_EGG =
            spawnEgg("goblin", ModEntities.GOBLIN, 0x8B9C6D, 0x523837);
    public static final RegistryObject<ForgeSpawnEggItem> HARPY_SPAWN_EGG =
            spawnEgg("harpy", ModEntities.HARPY, 0x3D3814, 0xA491EB);
    public static final RegistryObject<ForgeSpawnEggItem> FLAME_SPEWER_SPAWN_EGG =
            spawnEgg("flame_spewer", ModEntities.FLAME_SPEWER, 0x864FEE, 0x8D6BE6);
    public static final RegistryObject<ForgeSpawnEggItem> VOID_EYE_SPAWN_EGG =
            spawnEgg("void_eye", ModEntities.VOID_EYE, 0x0C0DD2, 0x8ED2F9);
    public static final RegistryObject<ForgeSpawnEggItem> YETI_SPAWN_EGG =
            spawnEgg("yeti", ModEntities.YETI, 0xE9F0F5, 0x9AB4C4);

    private static <T extends Item> RegistryObject<T> item(String name, Supplier<T> factory) {
        RegistryObject<T> registered = ITEMS.register(name, factory);
        TAB_ORDER.add(registered);
        return registered;
    }

    private static RegistryObject<ForgeSpawnEggItem> spawnEgg(String name, RegistryObject<? extends EntityType<? extends net.minecraft.world.entity.Mob>> type,
                                                              int background, int highlight) {
        return item(name + "_spawn_egg",
                () -> new ForgeSpawnEggItem(type, background, highlight, new Item.Properties()));
    }

    public static List<RegistryObject<? extends Item>> tabOrder() {
        return TAB_ORDER;
    }

    private ModItems() {
    }
}
