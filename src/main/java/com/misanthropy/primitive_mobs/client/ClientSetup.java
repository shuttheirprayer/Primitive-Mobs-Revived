package com.misanthropy.primitive_mobs.client;

import com.misanthropy.primitive_mobs.client.model.*;
import com.misanthropy.primitive_mobs.client.renderer.*;
import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.core.ModItems;
import com.misanthropy.primitive_mobs.item.WonderSapItem;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ClientSetup {
    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerLayerDefinitions);
        modBus.addListener(ClientSetup::registerRenderers);
        modBus.addListener(ClientSetup::registerItemColors);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ChameleonModel.LAYER, ChameleonModel::createBodyLayer);
        event.registerLayerDefinition(DodoModel.LAYER, DodoModel::createBodyLayer);
        event.registerLayerDefinition(EnchantedBookModel.LAYER, EnchantedBookModel::createBodyLayer);
        event.registerLayerDefinition(FilchLizardModel.LAYER, FilchLizardModel::createBodyLayer);
        event.registerLayerDefinition(FlameSpewerModel.LAYER, FlameSpewerModel::createBodyLayer);
        event.registerLayerDefinition(GoblinModel.LAYER, GoblinModel::createBodyLayer);
        event.registerLayerDefinition(GroveSpriteModel.LAYER, GroveSpriteModel::createBodyLayer);
        event.registerLayerDefinition(GroveSpriteLeavesModel.LAYER, GroveSpriteLeavesModel::createBodyLayer);
        event.registerLayerDefinition(HarpyModel.LAYER, HarpyModel::createBodyLayer);
        event.registerLayerDefinition(LilyLurkerModel.LAYER, LilyLurkerModel::createBodyLayer);
        event.registerLayerDefinition(LostMinerModel.LAYER, LostMinerModel::createBodyLayer);
        event.registerLayerDefinition(MimicModel.LAYER, MimicModel::createBodyLayer);
        event.registerLayerDefinition(SheepmanModel.LAYER, SheepmanModel::createBodyLayer);
        event.registerLayerDefinition(TravelingMerchantModel.LAYER, TravelingMerchantModel::createBodyLayer);
        event.registerLayerDefinition(TrollagerModel.LAYER, TrollagerModel::createBodyLayer);
        event.registerLayerDefinition(VoidEyeModel.LAYER, VoidEyeModel::createBodyLayer);
        event.registerLayerDefinition(BlazingJuggernautModel.LAYER, BlazingJuggernautModel::createBodyLayer);
        event.registerLayerDefinition(YetiModel.LAYER, YetiModel::createBodyLayer);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CHAMELEON.get(), ChameleonRenderer::new);
        event.registerEntityRenderer(ModEntities.TREASURE_SLIME.get(), TreasureSlimeRenderer::new);
        event.registerEntityRenderer(ModEntities.HAUNTED_TOOL.get(), HauntedToolRenderer::new);
        event.registerEntityRenderer(ModEntities.GROVE_SPRITE.get(), GroveSpriteRenderer::new);
        event.registerEntityRenderer(ModEntities.BEWITCHED_TOME.get(), BewitchedTomeRenderer::new);
        event.registerEntityRenderer(ModEntities.FILCH_LIZARD.get(), FilchLizardRenderer::new);
        event.registerEntityRenderer(ModEntities.BRAIN_SLIME.get(), BrainSlimeRenderer::new);
        event.registerEntityRenderer(ModEntities.ROCKET_CREEPER.get(), PrimitiveCreeperRenderer::new);
        event.registerEntityRenderer(ModEntities.FESTIVE_CREEPER.get(), PrimitiveCreeperRenderer::new);
        event.registerEntityRenderer(ModEntities.SUPPORT_CREEPER.get(), PrimitiveCreeperRenderer::new);
        event.registerEntityRenderer(ModEntities.SKELETON_WARRIOR.get(), SkeletonWarriorRenderer::new);
        event.registerEntityRenderer(ModEntities.BLAZING_JUGGERNAUT.get(), BlazingJuggernautRenderer::new);
        event.registerEntityRenderer(ModEntities.LILY_LURKER.get(), LilyLurkerRenderer::new);
        event.registerEntityRenderer(ModEntities.MOTHER_SPIDER.get(), MotherSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_SPIDER.get(), BabySpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.TROLLAGER.get(), TrollagerRenderer::new);
        event.registerEntityRenderer(ModEntities.LOST_MINER.get(), LostMinerRenderer::new);
        event.registerEntityRenderer(ModEntities.TRAVELING_MERCHANT.get(), TravelingMerchantRenderer::new);
        event.registerEntityRenderer(ModEntities.DODO.get(), DodoRenderer::new);
        event.registerEntityRenderer(ModEntities.MIMIC.get(), MimicRenderer::new);
        event.registerEntityRenderer(ModEntities.SHEEPMAN.get(), SheepmanRenderer::new);
        event.registerEntityRenderer(ModEntities.GOBLIN.get(), GoblinRenderer::new);
        event.registerEntityRenderer(ModEntities.HARPY.get(), HarpyRenderer::new);
        event.registerEntityRenderer(ModEntities.FLAME_SPEWER.get(), FlameSpewerRenderer::new);
        event.registerEntityRenderer(ModEntities.VOID_EYE.get(), VoidEyeRenderer::new);
        event.registerEntityRenderer(ModEntities.YETI.get(), YetiRenderer::new);

        event.registerEntityRenderer(ModEntities.PRIMITIVE_TNT.get(), PrimitiveTntRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_BLOCK.get(), ThrownBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.FLAME_SPIT.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.SPIDER_EGG_PROJECTILE.get(), ThrownItemRenderer::new);
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack),
                ModItems.CAMOUFLAGE_HELMET.get(), ModItems.CAMOUFLAGE_CHESTPLATE.get(),
                ModItems.CAMOUFLAGE_LEGGINGS.get(), ModItems.CAMOUFLAGE_BOOTS.get());
        event.register(ClientSetup::wonderSapColor, ModItems.WONDER_SAP.get());
    }

    private static int wonderSapColor(ItemStack stack, int tintIndex) {
        return tintIndex > 0 ? -1 : WonderSapItem.getColor(stack);
    }

    private ClientSetup() {
    }
}
