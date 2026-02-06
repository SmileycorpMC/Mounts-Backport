package net.smileycorp.mounts.config;


import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class LootConfig {

    private static List<LootTableEntry> lootTableEntries = null;

    public static void readConfig() {
        lootTableEntries = Lists.newArrayList();
        JsonParser parser = new JsonParser();
        File file = DataGenerator.CONFIG_FOLDER.toPath().resolve("loot_tables.json").toFile();
        try {
            for (JsonElement element : parser.parse(new FileReader(file)).getAsJsonArray())
                LootTableEntry.fromJson(lootTableEntries, element.getAsJsonObject());
        } catch (Exception e) {}
    }

    public static List<LootTableEntry> getLootTableEntries() {
        if (lootTableEntries == null) readConfig();
        return lootTableEntries;
    }

}
