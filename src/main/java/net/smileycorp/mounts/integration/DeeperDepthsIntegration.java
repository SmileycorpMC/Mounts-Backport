package net.smileycorp.mounts.integration;

import com.deeperdepths.common.entities.EntityBogged;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.smileycorp.mounts.config.MountsConfig;

import java.util.Random;
import java.util.Set;

public class DeeperDepthsIntegration {

    public static boolean canBoggedSpawn(Random rand, Set<BiomeDictionary.Type> types) {
        return types.contains(BiomeDictionary.Type.SWAMP) && rand.nextFloat() <= MountsConfig.boggedChance;
    }

    public static AbstractSkeleton getBogged(World world) {
        return new EntityBogged(world);
    }

}
