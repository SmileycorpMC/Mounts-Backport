package net.smileycorp.mounts.config;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.smileycorp.atlas.api.config.EntityAttributesEntry;
import net.smileycorp.mounts.common.MountsLogger;

import java.io.File;
import java.util.List;

public class EntityConfig {

    public static EntityAttributesEntry camel;
    public static EntityAttributesEntry camelHusk;
    public static float camelHuskCarpetChance;
    public static EntityAttributesEntry parched;
    public static EntityAttributesEntry skeletonRider;
    public static boolean zombieHorsesBurnInSunlight;
    public static boolean skeletonHorsesBurnInSunlight;

    //horse traps
    public static float horseTrapSpawnChance;
    public static boolean improvedHorseTraps;

    //jockeys
    public static float jockeyChance;
    private static String[] jockeyMountableEntitiesStr;
    private static List<Class<? extends EntityLiving>> jockeyMountableEntities;
    private static String[] jockeyRiderEntitiesStr;
    private static List<Class<? extends EntityLiving>> jockeyRiderEntities;

    //spear charging entities
    private static String[] chargingEntitiesStr;
    private static List<Class<? extends EntityLiving>> chargingEntities;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/entities.cfg"));
        try{
            config.load();
            camel = new EntityAttributesEntry(config, "camel", 0.20000000298023224D, 16, 0, 31, 0, 0, 0);
            camelHusk = new EntityAttributesEntry(config, "camel husk", 0.20000000298023224D, 16, 0, 31, 0, 0, 0);
            camelHuskCarpetChance = config.getFloat("carpet chance", "camel husk", 0.005f, 0, 1, "Chance for a camel husk to spawn wearing a carpet.");
            parched = new EntityAttributesEntry(config, "parched", 0.25, 32, 2, 16, 0, 0, 0);
            skeletonRider = new EntityAttributesEntry(config, "skeleton rider", 0.25, 32, 2, 20, 0, 0, 0);
            zombieHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "zombie horse", true, "Do zombie horses burn in sunlight? (Vanilla 1.21.11 feature)");
            skeletonHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "skeleton horse", false, "Do skeleton horses burn in sunlight? (Added in 25w41a, removed in 25w42a)");
            horseTrapSpawnChance = config.getFloat("horseTrapSpawnChance", "skeleton horse traps", 0.01f, 0, 1, "How often do Skeleton Horse traps spawn at lightning strikes? (Multiplied by regional difficulty) (Vanilla default is 0.01)");
            improvedHorseTraps = config.getBoolean("improvedHorseTraps", "skeleton horse traps", true, "Whether to spawn Skeleton Horsemen from horse traps instead of vanilla skeletons?");
            jockeyChance = config.getFloat( "jockeyChance", "jockeys", 0.15f, 0, 1, "Chance for a baby mob to spawn as a jockey. (Bedrock feature)");
            jockeyMountableEntitiesStr = config.getStringList("mountableEntities", "jockeys",
                    new String[] {"minecraft:chicken", "minecraft:sheep", "minecraft:pig", "minecraft:cow", "minecraft:mushroom_cow", "minecraft:ocelot", "minecraft:wolf",
                            "minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:zombie_horse", "minecraft:skeleton_horse", "minecraft:spider", "minecraft:cave_spider",
                            "minecraft:zombie", "minecraft:zombie_villager", "minecraft:husk", "minecraft:zombie_pigman", "oe:zombie_nautilius", "futuremc:panda", "nb:strider", "nb:piglin_zombie"},
                    "Which entities can jockeys seek out and ride?");
            jockeyRiderEntitiesStr = config.getStringList("riderEntities", "jockeys",
                    new String[] {"minecraft:zombie", "minecraft:zombie_villager", "minecraft:husk", "minecraft:zombie_pigman", "oe:drowned", "oe:pickled", "nb:piglin_zombie"},
                    "Which entities can spawn with jockey ai?");
            chargingEntitiesStr = config.getStringList("chargingEntities", "general",
                    new String[] {"minecraft:zombie", "minecraft:husk", "minecraft:zombie_pigman", "nb:piglin"},
                    "Entities that charge with spears when held.");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

    public static boolean canCharge(EntityLivingBase entity) {
        if (chargingEntities == null) {
            MountsLogger.blankLine();
            MountsLogger.heading("LOADING CHARGING ENTITIES");
            chargingEntities = Lists.newArrayList();
            for (String str : chargingEntitiesStr) {
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
                        chargingEntities.add((Class<? extends EntityLiving>) clazz);
                        MountsLogger.logInfo("Loaded charging entity" + clazz + " as " + clazz.getName());
                    } else {
                        throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                    }
                } catch (Exception e) {
                    MountsLogger.logError("Error adding charging entity " + str, e);
                }
            }
        }
        for (Class<? extends EntityLiving> clazz : chargingEntities) if (clazz == entity.getClass()) return true;
        return false;
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
