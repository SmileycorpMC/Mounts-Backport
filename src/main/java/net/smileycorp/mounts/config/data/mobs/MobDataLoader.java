package net.smileycorp.mounts.config.data.mobs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.smileycorp.atlas.api.data.Sorters;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.mobs.conditions.Condition;
import net.smileycorp.mounts.config.data.mobs.functions.SpawnFunction;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MobDataLoader extends JsonLoader {

    public static MobDataLoader INSTANCE;

    private final Map<String, MobDataEntry> mobEntries = Maps.newTreeMap(Sorters::string);

    private MobDataLoader(FMLPreInitializationEvent event) {
        super(new File(event.getModConfigurationDirectory().getPath() + "/mounts/mobs"));
    }

    @Override
    protected void dataInit() {
        MountsLogger.blankLine();
        MountsLogger.heading("LOADING MOB DATA");
    }

    @Override
    protected void readData(Map<String, JsonElement> data) {
        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            String name = entry.getKey();
            MountsLogger.logInfo("Loading mob entry " + name);
            try {
                JsonObject obj = entry.getValue().getAsJsonObject();
                List<EntityEntry> entities = Lists.newArrayList();
                JsonElement entityType = obj.get("entity_type");
                if (entityType.isJsonArray()) entityType.getAsJsonArray().forEach(json -> addType(entities, json));
                else addType(entities, entityType);
                if (entities.isEmpty()) throw new ParsingException("No valid entities specified");
                MobDataEntry.Builder builder = MobDataEntry.Builder.of(name.replace("/", "."), entities);
                if (obj.has("spawn_chance")) builder.spawnChance(obj.get("spawn_chance").getAsFloat());
                if (obj.has("spawn_egg")) builder.spawnEgg(obj.get("spawn_egg").getAsBoolean());
                if (obj.has("functions")) for (JsonElement element : obj.get("functions").getAsJsonArray()) {
                    JsonObject json = element.getAsJsonObject();
                    SpawnFunction function =  DataRegistry.readFunction(json);
                    if (function == null) throw new NullPointerException();
                    List<Condition> conditions = Lists.newArrayList();
                    if (json.has("conditions")) json.get("conditions").getAsJsonArray().forEach(condition ->
                            conditions.add(DataRegistry.readCondition(condition.getAsJsonObject())));
                    builder.function(function, conditions);
                }
                if (obj.has("conditions")) for (JsonElement condition : obj.get("conditions").getAsJsonArray())
                    builder.condition(DataRegistry.readCondition(condition.getAsJsonObject()));
                MobDataEntry mobEntry = builder.build();
                mobEntries.put(name, mobEntry);
                if (mobEntry.hasSpawnEgg()) mobEntry.registerSpawnEggs();
            } catch (Exception e) {
                MountsLogger.logError("Failed loading mob data entry " + name, error(e, name));
                MountsLogger.blankLine();
            }
        }
    }

    private void addType(List<EntityEntry> entities, JsonElement element) {
        ResourceLocation loc = new ResourceLocation(element.getAsString());
        EntityEntry entity = ForgeRegistries.ENTITIES.getValue(loc);
        if (entity == null) {
            MountsLogger.logInfo("Failed adding entity type " + loc + " as it is not registered.");
            return;
        }
        if (!EntityLiving.class.isAssignableFrom(entity.getEntityClass())) {
            MountsLogger.logInfo("Failed adding entity type " + loc + " as it is not an EntityLiving.");
            return;
        }
        entities.add(entity);
        MountsLogger.logInfo("Added entity type " + loc + ".");
    }

    public MobDataEntry get(String data) {
        return mobEntries.getOrDefault(data, null);
    }

    public static void init(FMLPreInitializationEvent event) {
        INSTANCE = new MobDataLoader(event);
    }

}
