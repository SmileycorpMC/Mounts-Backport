package net.smileycorp.mounts.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class MountsLootTables
{
    public static final ResourceLocation CAMEL_DROPS = Constants.loc("entities/camel");
    public static final ResourceLocation CAMEL_HUSK_DROPS = Constants.loc("entities/camel_husk");
    public static final ResourceLocation PARCHED_DROPS = Constants.loc("entities/parched");

    public static void registerLootTables()
    {
        LootTableList.register(CAMEL_DROPS);
        LootTableList.register(CAMEL_HUSK_DROPS);
        LootTableList.register(PARCHED_DROPS);
    }
}