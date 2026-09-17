package net.smileycorp.mounts.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ItemsConfig {

    public static boolean anvilNetheriteSpearRecipe;
    public static boolean forceAnvilNetheriteSpearRecipe;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/items.cfg"));
        try{
            config.load();
            anvilNetheriteSpearRecipe = config.getBoolean("anvilRecipe", "netherite spear", true, "Add an Anvil recipe for the Netherite Spear if Netherite exists but a Smithing Table mod is not installed?");
            forceAnvilNetheriteSpearRecipe = config.getBoolean("forceAnvilRecipe", "netherite spear", false, "Force the Anvil recipe for the Netherite Spear to be added? (anvilRecipe must be set to true)");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
