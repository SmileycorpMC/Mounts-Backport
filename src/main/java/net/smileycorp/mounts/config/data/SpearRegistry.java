package net.smileycorp.mounts.config.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.JsonLoader;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class SpearRegistry extends JsonLoader {

    public static SpearRegistry INSTANCE;
    private final Map<String, ItemSpear> SPEARS = Maps.newLinkedHashMap();

    private SpearRegistry(FMLPreInitializationEvent event) {
        super(new File(event.getModConfigurationDirectory().getPath() + "/mounts/spears"));
    }

    public Collection<ItemSpear> getSpears() {
        return SPEARS.values();
    }

    public ItemSpear getSpear(String type) {
        return SPEARS.get(type);
    }

    public ItemSpear registerSpear(String name, SpearDefinition definition) {
        return registerSpear(name, new ItemSpear(definition));
    }

    public ItemSpear registerSpear(String name, ItemSpear spear) {
        SPEARS.put(name, spear);
        return spear;
    }

    @Override
    protected void dataInit() {
        MountsLogger.blankLine();
        MountsLogger.heading("LOADING SPEAR DEFINITIONS");
    }

    @Override
    protected void readData(Map<String, JsonElement> data) {
        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            String name = entry.getKey();
            try {
                registerSpear(name, SpearDefinition.fromJson(name, entry.getValue().getAsJsonObject()));
                MountsLogger.logInfo("Registered spear definition " + name);
            } catch (Exception e) {
                MountsLogger.logError("Failed loading spear definition " + name, e);
            }
        }
    }

    public static void init(FMLPreInitializationEvent event) {
        INSTANCE = new SpearRegistry(event);
    }

}
