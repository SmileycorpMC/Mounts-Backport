package net.smileycorp.mounts.config;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.smileycorp.atlas.api.config.EntityAttributesEntry;
import net.smileycorp.atlas.api.data.Pair;
import net.smileycorp.atlas.api.util.RecipeUtils;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.SpawnEggRegistry;

import java.io.File;
import java.util.List;

public class EntityConfig {

    //camels
    public static EntityAttributesEntry camel;
    private static String[] camelFoodStr;
    private static List<ItemStack> camelFood;

    //camel husks
    public static EntityAttributesEntry camelHusk;
    public static float camelHuskCarpetChance;
    private static String[] camelHuskFoodStr;
    private static List<ItemStack> camelHuskFood;

    //parched
    public static EntityAttributesEntry parched;

    //skeleton riders
    public static EntityAttributesEntry skeletonRider;

    //skeleton horses
    public static boolean skeletonHorsesBurnInSunlight;
    public static boolean skeletonHorsesPanicWhenDamaged;

    //horse traps
    public static float horseTrapSpawnChance;
    public static boolean improvedHorseTraps;

    //zombie horses
    public static boolean zombieHorsesBurnInSunlight;
    public static boolean zombieHorsesPanicWhenDamaged;
    private static String[] zombieHorsesFoodStr;
    private static List<ItemStack> zombieHorsesFood;

    //jockeys
    public static float jockeyChance;
    private static String[] jockeyRiderEntitiesStr;
    private static List<Pair<Class<? extends EntityLiving>, NBTTagCompound>> jockeyRiderEntities;
    private static String[] jockeyMountableEntitiesStr;
    private static List<Class<? extends EntityLiving>> jockeyMountableEntities;

    //spear charging entities
    private static String[] chargingEntitiesStr;
    private static List<Class<? extends EntityLiving>> chargingEntities;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/mounts/entities.cfg"));
        try{
            config.load();
            //camels
            camel = new EntityAttributesEntry(config, "camel", 0.20000000298023224D, 16, 0, 31, 0, 0, 0);
            camelFoodStr = config.getStringList("food", "camel", new String[] {"minecraft:cactus"}, "Items that count as food for camels for the purposes of taming, luring and healing.");
            //camel husks
            camelHusk = new EntityAttributesEntry(config, "camel husk", 0.20000000298023224D, 16, 0, 31, 0, 0, 0);
            camelHuskCarpetChance = config.getFloat("carpet chance", "camel husk", 0.005f, 0, 1, "Chance for a camel husk to spawn wearing a carpet.");
            camelHuskFoodStr = config.getStringList("food", "camel husk", new String[] {"minecraft:rabbit_foot"}, "Items that count as food for camel husks for the purposes of taming, luring and healing.");
            //parched
            parched = new EntityAttributesEntry(config, "parched", 0.25, 32, 2, 16, 0, 0, 0);
            //skeleton riders
            skeletonRider = new EntityAttributesEntry(config, "skeleton rider", 0.25, 32, 2, 20, 0, 0, 0);
            //skeleton horses
            skeletonHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "skeleton horse", false, "Do skeleton horses burn in sunlight? (Added in 25w41a, removed in 25w42a)");
            skeletonHorsesPanicWhenDamaged = config.getBoolean("panicWhenDamaged", "skeleton horse", false, "Do Skeleton Horses panic when damaged? (Skeleton Horses no longer panic when taking damage as of 26.1-snapshot2)");
            //skeleton horse traps
            horseTrapSpawnChance = config.getFloat("horseTrapSpawnChance", "skeleton horse traps", 0.01f, 0, 1, "How often do Skeleton Horse traps spawn at lightning strikes? (Multiplied by regional difficulty) (Vanilla default is 0.01)");
            improvedHorseTraps = config.getBoolean("improvedHorseTraps", "skeleton horse traps", true, "Whether to spawn Skeleton Horsemen from horse traps instead of vanilla skeletons?");
            //zombie horses
            zombieHorsesBurnInSunlight = config.getBoolean("burnInSunlight", "zombie horse", true, "Do zombie horses burn in sunlight? (Vanilla 1.21.11 feature)");
            zombieHorsesFoodStr = config.getStringList("food", "zombie horse", new String[] {"minecraft:red_mushroom"}, "Items that count as food for zombie horses for the purposes of taming, luring and healing.");
            zombieHorsesPanicWhenDamaged = config.getBoolean("panicWhenDamaged", "zombie horse", false, "Do Zombie Horses panic when damaged? (Zombie Horses no longer panic when taking damage as of 26.1-snapshot2)");
            //jockeys
            jockeyChance = config.getFloat( "jockeyChance", "jockeys", 0.15f, 0, 1, "Chance for a baby mob to spawn as a jockey. (Bedrock feature)");
            jockeyMountableEntitiesStr = config.getStringList("mountableEntities", "jockeys",
                    new String[] {"minecraft:chicken", "minecraft:sheep", "minecraft:pig", "minecraft:cow", "minecraft:mushroom_cow", "minecraft:ocelot", "minecraft:wolf",
                            "minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:zombie_horse", "minecraft:skeleton_horse", "minecraft:spider", "minecraft:cave_spider",
                            "minecraft:zombie", "minecraft:zombie_villager", "minecraft:husk", "minecraft:zombie_pigman", "oe:zombie_nautilius", "futuremc:panda", "nb:strider", "nb:piglin_zombie"},
                    "Which entities can jockeys seek out and ride?");
            jockeyRiderEntitiesStr = config.getStringList("riderEntities", "jockeys",
                    new String[] {"minecraft:zombie{IsBaby:1b}", "minecraft:zombie_villager{IsBaby:1b}", "minecraft:husk{IsBaby:1b}", "minecraft:zombie_pigman{IsBaby:1b}", "oe:drowned{IsBaby:1b}", "oe:pickled{IsBaby:1b}", "nb:piglin_zombie{IsBaby:1b}", "mekanism:babyskeleton"},
                    "Which entities can spawn with jockey ai?");
            //charging entities
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
                        MountsLogger.logInfo("Loaded charging entity " + str + " as " + clazz.getName());
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
            MountsLogger.blankLine();
            MountsLogger.heading("LOADING JOCKEY MOUNT ENTITIES");
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
                        MountsLogger.logInfo("Loaded jockey mountable entity " + str + " as " + clazz.getName());
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

    public static void initJockeyRiders() {
        MountsLogger.blankLine();
        MountsLogger.heading("LOADING JOCKEY RIDER ENTITIES");
        jockeyRiderEntities = Lists.newArrayList();
        for (String str : jockeyRiderEntitiesStr) {
            try {
                EntityEntry entry = null;
                NBTTagCompound nbt = null;
                int nbtStart = str.indexOf("{");
                if (nbtStart > -1) {
                    nbt = JsonToNBT.getTagFromJson(str.substring(nbtStart));
                    str = str.substring(0, nbtStart);
                }
                //check if it matches the syntax for a registry name
                if (str.contains(":")) {
                    ResourceLocation loc = new ResourceLocation(str);
                    if (GameData.getEntityRegistry().containsKey(loc)) {
                        entry = GameData.getEntityRegistry().getValue(loc);
                    } else continue;
                }
                if (entry == null) throw new Exception("Entry " + str + " is not in the correct format");
                Class<? extends Entity> clazz = entry.getEntityClass();
                if (EntityLiving.class.isAssignableFrom(clazz)) {
                    jockeyRiderEntities.add(Pair.of((Class<? extends EntityLiving>) clazz, nbt));
                    SpawnEggRegistry.register(new SpawnEggRegistry.JockeyEggEntry("jockey." + str.replace(":", "."), entry, nbt));
                    MountsLogger.logInfo("Loaded jockey rider entity " + str + " as " + clazz.getName());
                } else {
                    throw new Exception("Entity " + str + " is not an instance of EntityLiving");
                }
            } catch (Exception e) {
                MountsLogger.logError("Error adding jockey rider entity " + str, e);
            }
        }
    }

    public static boolean isJockeyRider(Entity entity) {
        if (!(entity instanceof EntityLiving) |! entity.isEntityAlive()) return false;
        if (jockeyRiderEntities == null) initJockeyRiders();
        for (Pair<Class<? extends EntityLiving>, NBTTagCompound> pair : jockeyRiderEntities) if (pair.getFirst() == entity.getClass() &&
                    (pair.getSecond() == null || NBTUtil.areNBTEquals(pair.getSecond(), entity.writeToNBT(new NBTTagCompound()), true))) return true;
        return false;
    }

    public static boolean isCamelFood(ItemStack stack) {
        if (camelFood == null) {
            MountsLogger.blankLine();
            MountsLogger.heading("LOADING CAMEL FOOD");
            camelFood = Lists.newArrayList();
            parseItems(camelFood, camelFoodStr);
        }
        for (ItemStack stack1 : camelFood) if (RecipeUtils.compareItemStacks(stack, stack1, true)) return true;
        return false;
    }

    public static boolean isCamelHuskFood(ItemStack stack) {
        if (camelHuskFood == null) {
            MountsLogger.blankLine();
            MountsLogger.heading("LOADING CAMEL HUSK FOOD");
            camelHuskFood = Lists.newArrayList();
            parseItems(camelHuskFood, camelHuskFoodStr);
        }
        for (ItemStack stack1 : camelHuskFood) if (RecipeUtils.compareItemStacks(stack, stack1, true)) return true;
        return false;
    }

    public static boolean isZombieHorseFood(ItemStack stack) {
        if (zombieHorsesFood == null) {
            MountsLogger.blankLine();
            MountsLogger.heading("LOADING ZOMBIE HORSE FOOD");
            zombieHorsesFood = Lists.newArrayList();
            parseItems(zombieHorsesFood, zombieHorsesFoodStr);
        }
        for (ItemStack stack1 : zombieHorsesFood) if (RecipeUtils.compareItemStacks(stack, stack1, true)) return true;
        return false;
    }

    private static void parseItems(List<ItemStack> stacks, String[] strings) {
        for (String name : strings) {
            NBTTagCompound nbt = null;
            if (name.contains("{")) {
                String nbtstring = name.substring(name.indexOf("{"));
                name = name.substring(0, name.indexOf("{"));
                try {
                    NBTTagCompound parsed = JsonToNBT.getTagFromJson(nbtstring);
                    if (parsed != null) nbt = parsed;
                } catch (Exception e) {
                    MountsLogger.logError("Error parsing nbt for stack " + name + " " + e.getMessage(), e);
                }
            }
            String[] nameSplit = name.split(":");
            if (nameSplit.length >= 2) {
                ResourceLocation loc = new ResourceLocation(nameSplit[0], nameSplit[1]);
                int meta;
                try {
                    meta = nameSplit.length > 2 ? (nameSplit[2].equals("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(nameSplit[2])) : 0;
                } catch (Exception e) {
                    meta = 0;
                    MountsLogger.logError("Entry" + name + " has a non integer, non wildcard metadata value", e);
                }
                if (ForgeRegistries.ITEMS.containsKey(loc)) {
                    ItemStack stack1 = new ItemStack(ForgeRegistries.ITEMS.getValue(loc), 1, meta);
                    if (nbt != null) stack1.setTagCompound(nbt);
                    MountsLogger.logInfo("Loaded item " + stack1);
                    stacks.add(stack1);
                }
            } else {
                MountsLogger.logError(name + " is not a valid registry", new NullPointerException());
            }
        }
    }

}
