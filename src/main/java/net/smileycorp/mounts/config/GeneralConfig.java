package net.smileycorp.mounts.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.atlas.api.config.EntityAttributesEntry;

import java.io.File;

public class GeneralConfig {

    public static boolean anvilNetheriteSpearRecipe;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/general.cfg"));
        try{
            config.load();
            anvilNetheriteSpearRecipe = config.getBoolean("anvilNetheriteSpearRecipe", "general", true, "Add an anvil recipe for the netherite spear if netherite exists but futuremc is not installed?");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
