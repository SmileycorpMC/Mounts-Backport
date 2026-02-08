package net.smileycorp.mounts.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.atlas.api.config.EntityAttributesEntry;

import java.io.File;

public class EntityConfig {

    public static EntityAttributesEntry camel;
    public static EntityAttributesEntry camelHusk;
    public static EntityAttributesEntry parched;
    public static boolean zombieHorsesBurnInSunlight;
    public static boolean skeletonHorsesBurnInSunlight;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/entities.cfg"));
        try{
            config.load();
            camel = new EntityAttributesEntry(config, "camel", 0.20000000298023224D, 16, 0, 31, 0, 0, 0);
            camelHusk = new EntityAttributesEntry(config, "camel husk", 0.20000000298023224D, 16, 0, 31, 0, 0, 0);
            parched = new EntityAttributesEntry(config, "parched", 0.25, 32, 2, 16, 0, 0, 0);
            zombieHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "zombie horse", true, "Do zombie horses burn in sunlight? (Vanilla 1.21.11 feature)");
            skeletonHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "skeleton horse", false, "Do zombie horses burn in sunlight? (Vanilla 1.21.11 feature)");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
