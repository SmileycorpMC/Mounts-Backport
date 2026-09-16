package net.smileycorp.mounts.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class TweaksFixesConfig {

    //tweaks
    public static boolean babyZombiePiggyback;
    public static boolean vanillaSpiderJockeysSpawn;


    //fixes
    public static boolean ridersControlAnimals;
    public static boolean passengersCantHitEachother;
    public static boolean controlledMountsOwnPathing;
    public static boolean horsesDontDismountMobs;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/tweaks_fixes.cfg"));
        try{
            config.load();
            //tweaks
            babyZombiePiggyback = config.getBoolean("babyZombiePiggyback", "tweaks", true, "Do zombies carry baby mobs in a piggyback? (used for the bedrock jockeys feature)");
            vanillaSpiderJockeysSpawn = config.getBoolean("vanillaSpiderJockeysSpawn", "tweaks", false, "Do vanilla spider jockeys spawn? (not needed if the entry exists in the config)");
            //fixes
            ridersControlAnimals = config.getBoolean("ridersControlAnimals", "fixes", true, "Are animals movements controlled by their rider? (Used to make jockey mobs work)");
            passengersCantHitEachother = config.getBoolean("passengersCantHitEachother", "fixes", true, "Are projectiles shot by passengers prevented from hitting other passengers on the same entity? (Required to prevent camel husk jockey infighting)");
            controlledMountsOwnPathing = config.getBoolean("controlledMountsOwnPathing", "fixes", true, "Do controlled mounted entities use their own pathing instead of the controlling passenger's pathing? (Fixes issues with large hitbox mounts getting stuck on the edges of blocks, also required to prevent camel husk jockey ai bugging)");
            horsesDontDismountMobs = config.getBoolean("horsesDontDismountMobs", "fixes", true, "Are horses prevented from dismounting mobs that are riding them? (Fixes baby zombie jockeys not being able to ride horses)");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

}
