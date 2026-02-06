package net.smileycorp.mounts.config;

import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.common.MountsLogger;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Map;

public class SpearRegistry {

    private static Map<String, ItemSpear> SPEARS = null;

    public static Collection<ItemSpear> getSpears() {
        if (SPEARS == null) readConfig();
        return SPEARS.values();
    }

    public static ItemSpear getSpear(String type) {
        return SPEARS.get(type);
    }

    public static ItemSpear registerSpear(String name, SpearDefinition definition) {
        return registerSpear(name, new ItemSpear(definition));
    }

    public static ItemSpear registerSpear(String name, ItemSpear spear) {
        SPEARS.put(name, spear);
        return spear;
    }

    private static void readConfig() {
        SPEARS = Maps.newLinkedHashMap();
        File directory = DataGenerator.CONFIG_FOLDER.toPath().resolve("spears").toFile();
        if (!directory.exists()) return;
        JsonParser parser = new JsonParser();
        for (File file : directory.listFiles((f, s) -> s.endsWith(".json"))) {
            String name = file.getName().replace(".json", "");
            try {
                MountsLogger.logInfo("Registered spear definition " + name);
                registerSpear(name, SpearDefinition.fromJson(name, parser.parse(new FileReader(file)).getAsJsonObject()));
            } catch (Exception e) {
                MountsLogger.logError("Failed loading spear definition " + name, e);
            }
        }
    }

}
