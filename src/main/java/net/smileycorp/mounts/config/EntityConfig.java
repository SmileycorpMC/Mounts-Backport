package net.smileycorp.mounts.config;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
    public static EntityAttributesEntry skeletonHorseman;
    public static boolean zombieHorsesBurnInSunlight;
    public static boolean skeletonHorsesBurnInSunlight;
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
            skeletonHorseman = new EntityAttributesEntry(config, "skeleton horseman", 0.25, 32, 2, 20, 0, 0, 0);
            zombieHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "zombie horse", true, "Do zombie horses burn in sunlight? (Vanilla 1.21.11 feature)");
            skeletonHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "skeleton horse", false, "Do skeleton horses burn in sunlight? (Added in 25w41a, removed in 25w42a)");
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
    
}
