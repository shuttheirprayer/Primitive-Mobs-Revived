package com.misanthropy.primitive_mobs.core;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class PrimitiveMobsConfig {
    public static final ForgeConfigSpec SPEC;
    public static final PrimitiveMobsConfig CONFIG;

    public final ForgeConfigSpec.BooleanValue festiveCreeperDestruction;
    public final ForgeConfigSpec.IntValue maxSpiderFamilySize;
    public final ForgeConfigSpec.BooleanValue lostMinerSounds;
    public final ForgeConfigSpec.BooleanValue trollagerDestruction;
    public final ForgeConfigSpec.IntValue tameableSlimeChance;
    public final ForgeConfigSpec.IntValue filchLizardLootChance;
    public final ForgeConfigSpec.BooleanValue merchantCanSettle;
    public final ForgeConfigSpec.BooleanValue dodoMycelium;
    public final ForgeConfigSpec.BooleanValue merchantVisitsHouses;
    public final ForgeConfigSpec.BooleanValue rocketCreeperAlwaysJumps;
    public final ForgeConfigSpec.BooleanValue hauntedToolFullDurability;
    public final ForgeConfigSpec.BooleanValue groveSpritesPlant;
    public final ForgeConfigSpec.IntValue lostMinerRewardBase;
    public final ForgeConfigSpec.IntValue lostMinerRewardBonus;
    public final ForgeConfigSpec.IntValue mimicChestRarity;
    public final ForgeConfigSpec.IntValue naturalSpawnRatePercent;

    static {
        Pair<PrimitiveMobsConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(PrimitiveMobsConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    private PrimitiveMobsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("mobs");
        festiveCreeperDestruction = builder
                .comment("Whether TNT thrown by Festive Creepers damages terrain")
                .define("festiveCreeperDestruction", true);
        maxSpiderFamilySize = builder
                .comment("Maximum number of Baby Spiders spawning alongside a Mother Spider")
                .defineInRange("maxSpiderFamilySize", 6, 0, 32);
        lostMinerSounds = builder
                .comment("Whether the Lost Miner makes villager sounds")
                .define("lostMinerSounds", true);
        trollagerDestruction = builder
                .comment("Whether the Trollager can tear blocks out of the terrain to throw")
                .define("trollagerDestruction", true);
        tameableSlimeChance = builder
                .comment("Percentage chance that a spawning Treasure Slime is tameable")
                .defineInRange("tameableSlimeChance", 5, 0, 100);
        filchLizardLootChance = builder
                .comment("Percentage chance that a Filch Lizard spawns holding loot")
                .defineInRange("filchLizardLootChance", 25, 0, 100);
        merchantCanSettle = builder
                .comment("Whether the Traveling Merchant can settle down after being bribed with a block of emeralds")
                .define("merchantCanSettle", true);
        dodoMycelium = builder
                .comment("Whether Dodos slowly convert grass blocks into mycelium")
                .define("dodoMycelium", true);
        merchantVisitsHouses = builder
                .comment("Whether the Traveling Merchant walks indoors")
                .define("merchantVisitsHouses", true);
        rocketCreeperAlwaysJumps = builder
                .comment("Whether Rocket Creepers launch even without headroom")
                .define("rocketCreeperAlwaysJumps", false);
        hauntedToolFullDurability = builder
                .comment("Whether Haunted Tools drop their weapon at full durability")
                .define("hauntedToolFullDurability", false);
        groveSpritesPlant = builder
                .comment("Whether Grove Sprites plant the saplings they carry")
                .define("groveSpritesPlant", true);
        lostMinerRewardBase = builder
                .comment("Minimum number of emeralds a rescued Lost Miner rewards")
                .defineInRange("lostMinerRewardBase", 2, 0, 64);
        lostMinerRewardBonus = builder
                .comment("Additional random emeralds a rescued Lost Miner may reward")
                .defineInRange("lostMinerRewardBonus", 2, 0, 64);
        mimicChestRarity = builder
                .comment("How rare Mimic chests are in caves: higher is rarer, 0 disables them")
                .defineInRange("mimicChestRarity", 25, 0, 1000);
        builder.pop();

        builder.push("spawning");
        naturalSpawnRatePercent = builder
                .comment("Percentage of natural Primitive Mobs spawns that are allowed to proceed.",
                        "Lower this in large modpacks where mobs compete for spawn slots and Primitive Mobs feel too frequent.",
                        "Does not affect eggs, breeding, or mobs spawned by other mobs (e.g. Baby Spiders).")
                .defineInRange("naturalSpawnRatePercent", 100, 0, 100);
        builder.pop();
    }
}
