package net.smileycorp.mounts.config.data;


import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.LootTableEntry;
import net.smileycorp.mounts.config.data.mobs.JsonLoader;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LootRegistry extends JsonLoader {

    public static LootRegistry INSTANCE;

    private List<LootTableEntry> lootTableEntries;

    private LootRegistry(FMLPreInitializationEvent event) {
        super(new File(event.getModConfigurationDirectory().getPath() + "/mounts/loot_tables.json"));
    }

    @Override
    protected void dataInit() {
        MountsLogger.blankLine();
        MountsLogger.heading("LOADING LOOT TABLE DATA");
        lootTableEntries = Lists.newArrayList();
    }

    @Override
    protected void readData(Map<String, JsonElement> data) {
        for (JsonElement element : data.get("loot_tables").getAsJsonArray())
            LootTableEntry.fromJson(lootTableEntries, element.getAsJsonObject());
    }

    public List<LootTableEntry> getLootTableEntries() {
        if (lootTableEntries == null) loadData();
        return lootTableEntries;
    }

    public static void init(FMLPreInitializationEvent event) {
        INSTANCE = new LootRegistry(event);
    }

}
