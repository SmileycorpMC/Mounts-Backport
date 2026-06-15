package net.smileycorp.mounts.config;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.smileycorp.mounts.common.MountsLogger;

import java.io.File;
import java.util.List;

public class MountsConfig {

    //baby zombies
    public static float jockeyChance;
    private static String[] jockeyMountableEntitiesStr;
    private static List<Class<? extends EntityLiving>> jockeyMountableEntities;
    private static String[] jockeyRiderEntitiesStr;
    private static List<Class<? extends EntityLiving>> jockeyRiderEntities;

    //spiders
    public static float spiderJockeyChance;
    public static float caveSpiderJockeyChance;
    public static float strayChance;
    public static float parchedChance;
    public static float boggedChance;

    //camels
    public static float huskJockeyChance;

    //horse traps
    public static float horseTrapSpawnChance;
    public static boolean improvedHorseTraps;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/mounts.cfg"));
        try{
            config.load();
            jockeyChance = config.getFloat( "jockeyChance", "jockeys", 0.15f, 0, 1, "Chance for a baby mob to spawn as a jockey. (Bedrock feature)");
            jockeyMountableEntitiesStr = config.getStringList("mountableEntities", "jockeys",
                    new String[] {"minecraft:chicken", "minecraft:sheep", "minecraft:pig", "minecraft:cow", "minecraft:mushroom_cow", "minecraft:ocelot", "minecraft:wolf",
                            "minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:zombie_horse", "minecraft:skeleton_horse", "minecraft:spider", "minecraft:cave_spider",
                            "minecraft:zombie", "minecraft:zombie_villager", "minecraft:husk", "minecraft:zombie_pigman", "oe:zombie_nautilius", "futuremc:panda", "nb:strider"},
                    "Which entities can baby zombies seek out and ride?");
            jockeyRiderEntitiesStr = config.getStringList("riderEntities", "jockeys",
                    new String[] {"minecraft:zombie", "minecraft:zombie_villager", "minecraft:husk", "minecraft:zombie_pigman", "oe:drowned", "oe:pickled"},
                    "Which entities can baby zombies seek out and ride?");
            spiderJockeyChance = config.getFloat( "spiderJockeyChance", "spiders", 0.01f, 0, 1, "Chance for a spider to spawn as a skeleton jockey.");
            caveSpiderJockeyChance = config.getFloat( "caveSpiderJockeyChance", "spiders", 0.01f, 0, 1, "Chance for a cave spider to spawn as a skeleton jockey. (Bedrock feature)");
            strayChance = config.getFloat( "strayChance", "spiders", 0.8f, 0, 1, "Chance for a skeleton jockey to be replaced with a stray jockey in snowy biomes. (Bedrock feature)");
            parchedChance = config.getFloat( "parchedChance", "spiders", 0.8f, 0, 1, "Chance for a skeleton jockey to be replaced with a parched jockey in deserts. (Bedrock feature)");
            boggedChance = config.getFloat( "boggedChance", "spiders", 0.8f, 0, 1, "Chance for a skeleton jockey to be replaced with a bogged jockey in swamps. (Bedrock feature) (Only if Deeper Depths is installed)");
            huskJockeyChance = config.getFloat( "huskJockeyChance", "zombies", 0.1f, 0, 1, "Chance for a husk to spawn as a husk jockey.");
            horseTrapSpawnChance = config.getFloat("horseTrapSpawnChance", "skeleton horse traps", 0.01f, 0, 1, "How often do Skeleton Horse traps spawn at lightning strikes? (Multiplied by regional difficulty) (Vanilla default is 0.01)");
            improvedHorseTraps = config.getBoolean("improvedHorseTraps", "skeleton horse traps", true, "Whether to spawn Skeleton Horsemen from horse traps instead of vanilla skeletons?");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

    public static boolean isJockeyMount(EntityLivingBase entity) {
        if (entity.isChild() || entity.isRiding() || entity.isBeingRidden() |! entity.isEntityAlive()) return false;
        if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null) return false;
        if (jockeyMountableEntities == null) {
            jockeyMountableEntities = Lists.newArrayList();
            for (String str : jockeyMountableEntitiesStr) {
                try {
                    Class<?> clazz = null;
                    //check if it matches the syntax for a registry name
                    if (str.contains(":")) {
                        ResourceLocation loc = new ResourceLocation(str);
                        if (GameData.getEntityRegistry().containsKey(loc)) {
                            clazz = GameData.getEntityRegistry().getValue(loc).getEntityClass();
                        } else continue;
                    }
                    if (clazz == null) throw new Exception("Entry " + str + " is not in the correct format");
                    if (EntityLiving.class.isAssignableFrom(clazz)) {
                        jockeyMountableEntities.add((Class<? extends EntityLiving>) clazz);
                        MountsLogger.logInfo("Loaded jockey mountable entity" + clazz + " as " + clazz.getName());
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                   MountsLogger.logError("Error adding jockey mountable entity " + str, e);
                }
            }
        }
        for (Class<? extends EntityLiving> clazz : jockeyMountableEntities) if (clazz == entity.getClass()) return true;
        return false;
    }

    public static boolean isJockeyRider(EntityLivingBase entity) {
        if (!entity.isChild() || entity.isRiding() || entity.isBeingRidden() |! entity.isEntityAlive()) return false;
        if (jockeyRiderEntities == null) {
            jockeyRiderEntities = Lists.newArrayList();
            for (String str : jockeyRiderEntitiesStr) {
                try {
                    Class<?> clazz = null;
                    //check if it matches the syntax for a registry name
                    if (str.contains(":")) {
                        ResourceLocation loc = new ResourceLocation(str);
                        if (GameData.getEntityRegistry().containsKey(loc)) {
                            clazz = GameData.getEntityRegistry().getValue(loc).getEntityClass();
                        } else continue;
                    }
                    if (clazz == null) throw new Exception("Entry " + str + " is not in the correct format");
                    if (EntityLiving.class.isAssignableFrom(clazz)) {
                        jockeyRiderEntities.add((Class<? extends EntityLiving>) clazz);
                        MountsLogger.logInfo("Loaded jockey rider entity" + clazz + " as " + clazz.getName());
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                    MountsLogger.logError("Error adding jockey rider entity " + str, e);
                }
            }
        }
        for (Class<? extends EntityLiving> clazz : jockeyRiderEntities) if (clazz == entity.getClass()) return true;
        return false;
    }

}
