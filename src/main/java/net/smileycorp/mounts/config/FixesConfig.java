package net.smileycorp.mounts.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class FixesConfig {

    //mounts general
    public static boolean ridersControlAnimals;
    public static boolean controlledMountsOwnPathing;
    public static boolean passengersCantHitEachother;
    //horses
    public static boolean horsesDontDismountMobs;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/fixes.cfg"));
        try{
            config.load();
            ridersControlAnimals = config.getBoolean("ridersControlAnimals", "mounts general", true, "Are animals movements controlled by their rider? (Used to make jockey mobs work)");
            controlledMountsOwnPathing = config.getBoolean("controlledMountsOwnPathing", "mounts general", true, "Do controlled mounted entities use their own pathing instead of the controlling passenger's pathing? (Fixes issues with large hitbox mounts getting stuck on the edges of blocks, also required to prevent camel husk jockey ai bugging)");
            passengersCantHitEachother = config.getBoolean("passengersCantHitEachother", "mounts general", true, "Are projectiles shot by passengers prevented from hitting other passengers on the same entity? (Required to prevent camel husk jockey infighting)");
            horsesDontDismountMobs = config.getBoolean("horsesDontDismountMobs", "horses", true, "Are horses prevented from dismounting mobs that are riding them? (Fixes baby zombie jockeys not being able to ride horses)");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

}
