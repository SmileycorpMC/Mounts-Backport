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
    public static float babyZombieJockeyChance;
    private static String[] babyZombieMountableEntitiesStr;
    private static List<Class<? extends EntityLiving>> babyZombieMountableEntities;

    //spiders
    public static float spiderJockeyChance;
    public static float caveSpiderJockeyChance;
    public static float strayChance;
    public static float parchedChance;
    public static float boggedChance;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/mounts.cfg"));
        try{
            config.load();
            babyZombieJockeyChance = config.getFloat( "jockeyChance", "baby zombies", 0.15f, 0, 1, "Chance for a baby zombie to spawn as a jockey. (Bedrock feature)");
            babyZombieMountableEntitiesStr = config.getStringList("mountableEntities", "baby zombies",
                    new String[] {"minecraft:chicken", "minecraft:sheep", "minecraft:pig", "minecraft:cow", "minecraft:mushroom_cow", "minecraft:ocelot", "minecraft:wolf",
                            "minecraft:horse", "minecraft:donkey", "minecraft:mule", "miencraft:zombie_horse", "minecraft:skeleton_horse", "minecraft:spider", "minecraft:cave_spider",
                            "minecraft:zombie", "minecraft:husk", "minecraft:zombie_pigman", "oe:zombie_nautilius", "futuremc:panda"},
                    "Which entities can baby zombies seek out and ride?");
            spiderJockeyChance = config.getFloat( "spiderJockeyChance", "spiders", 0.01f, 0, 1, "Chance for a spider to spawn as a skeleton jockey.");
            caveSpiderJockeyChance = config.getFloat( "caveSpiderJockeyChance", "spiders", 0.01f, 0, 1, "Chance for a cave spider to spawn as a skeleton jockey. (Bedrock feature)");
            strayChance = config.getFloat( "strayChance", "spiders", 0.8f, 0, 1, "Chance for a skeleton jockey to be replaced with a stray jockey in snowy biomes. (Bedrock feature)");
            parchedChance = config.getFloat( "parchedChance", "spiders", 0.8f, 0, 1, "Chance for a skeleton jockey to be replaced with a parched jockey in deserts. (Bedrock feature)");
            boggedChance = config.getFloat( "boggedChance", "spiders", 0.8f, 0, 1, "Chance for a skeleton jockey to be replaced with a bogged jockey in swamps. (Bedrock feature) (Only if deeper depths is installed)");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

    public static boolean canBabyZombieMount(EntityLivingBase entity) {
        if (entity.isChild() || entity.isRiding() || entity.isBeingRidden() |! entity.isEntityAlive()) return false;
        if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null) return false;
        if (babyZombieMountableEntities == null) {
            babyZombieMountableEntities = Lists.newArrayList();
            for (String str : babyZombieMountableEntitiesStr) {
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
                        babyZombieMountableEntities.add((Class<? extends EntityLiving>) clazz);
                        MountsLogger.logInfo("Loaded baby zombie mountable entity" + clazz + " as " + clazz.getName());
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                   MountsLogger.logError("Error adding baby zombie mountable entity " + str, e);
                }
            }
        }
        for (Class<? extends EntityLiving> clazz : babyZombieMountableEntities) if (clazz.isAssignableFrom(entity.getClass())) return true;
        return false;
    }

}
