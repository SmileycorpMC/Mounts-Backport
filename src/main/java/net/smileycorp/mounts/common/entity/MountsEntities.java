package net.smileycorp.mounts.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.Mounts;

public class MountsEntities
{
    public static int id;

    public static void registerEntities()
    {
        registerEntity("camel", EntityCamel.class, ++id, 80, 16565097, 13341495);
        registerEntity("camel_husk", EntityCamelHusk.class, ++id, 80, 16565097, 13341495);
        registerEntity("parched", EntityParched.class, ++id, 80, 11376223, 6249550);
    }

    public static void registerEntitySpawns()
    {
        EntityRegistry.addSpawn(EntityCamel.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.DESERT);
        EntityRegistry.addSpawn(EntityCamel.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.DESERT_HILLS);
    }

    private static void registerEntity(String name, Class<? extends Entity> entity, int id, int range, int color1, int color2)
    { EntityRegistry.registerModEntity(new ResourceLocation(Constants.MODID, name), entity, Constants.MODID + "." + name, id, Mounts.instance, range, 1, true, color1, color2); }

    private static void registerEntity(String name, Class<? extends Entity> entity, int id, int range)
    { EntityRegistry.registerModEntity(new ResourceLocation(Constants.MODID, name), entity, Constants.MODID + "." + name, id, Mounts.instance, range, 1, true); }

    private static void spawnRate(Class<? extends EntityLiving> entityClass, EnumCreatureType creatureType, int weight, int min, int max, BiomeDictionary.Type biomesAllowed)
    {
        for(Biome biome: BiomeDictionary.getBiomes(biomesAllowed))
        {
            if(biome != null && weight > 0)
            { EntityRegistry.addSpawn(entityClass, weight, min, max, creatureType, biome); }
        }
    }

}
