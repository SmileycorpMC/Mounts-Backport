package net.smileycorp.mounts.config;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;

public class LootTableEntry {

    private final String name;
    private final int weight, quality;
    private final ResourceLocation lootTable;
    private final String pool;
    private final Item item;
    private LootFunction[] functions;

    public LootTableEntry(Configuration config, Collection<LootTableEntry> entries, String key, ResourceLocation lootTable, String pool, int weight, Item item) {
        this(config, entries, key, lootTable, pool, weight, item, 0);
    }

    public LootTableEntry(Configuration config, Collection<LootTableEntry> entries, String key, ResourceLocation lootTable, String pool, int weight, Item item, int metadata) {
        this(entries, lootTable, pool, config.get("general", key, weight, "Weight for " + item.getRegistryName()
                + " to generate in " + lootTable + " (Set to 0  to disable)").getInt(), 0, item, metadata);
    }

    public LootTableEntry(Collection<LootTableEntry> entries, ResourceLocation lootTable, String pool, int weight, int quality, Item item, int metadata) {
        this.name = item.getRegistryName() + (metadata == 0 ? "" : "_" + metadata);
        this.weight = weight;
        this.quality = quality;
        this.lootTable = lootTable;
        this.pool = pool;
        this.item = item;
        functions = new LootFunction[]{new SetMetadata(new LootCondition[0], new RandomValueRange(metadata))};
        entries.add(this);
    }

    public void addFunction(LootFunction... functions) {
        this.functions = ArrayUtils.addAll(this.functions, functions);
    }

    public boolean canApply(ResourceLocation lootTable) {
        if (weight <= 0) return false;
        return this.lootTable.equals(lootTable);
    }

    public void addEntry(LootTable table) {
        table.getPool(pool).addEntry(new LootEntryItem(item, weight, quality, functions, new LootCondition[0], name));
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getQuality() {
        return quality;
    }

    public ResourceLocation getLootTable() {
        return lootTable;
    }

    public String getPool() {
        return pool;
    }

    public Item getItem() {
        return item;
    }

    public LootFunction[] getFunctions() {
        return functions;
    }

    public static LootTableEntry fromJson(Collection<LootTableEntry> entries, JsonObject json) {
        Gson gson = new Gson();
        JsonDeserializationContext ctx = gson::fromJson;
        if (!json.has("loot_table") |! json.has("item")) return null;
        try {
            ResourceLocation lootTable = new ResourceLocation(json.get("loot_table").getAsString());
            String pool = json.has("pool") ? json.get("pool").getAsString() : "main";
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("item").getAsString()));
            int meta = json.has("meta") ? json.get("meta").getAsInt() : 0;
            int weight = json.has("weight") ? json.get("weight").getAsInt() : 1;
            int quality = json.has("quality") ? json.get("quality").getAsInt() : 0;
            LootTableEntry entry = new LootTableEntry(entries, lootTable, pool, weight, quality, item, meta);
            if (json.has("functions")) for (JsonElement element : json.get("functions").getAsJsonArray()) {
                JsonObject obj = (JsonObject) element;
                if (!obj.has("function")) continue;
                entry.addFunction(LootFunctionManager.getSerializerForName(new ResourceLocation(obj.get("function").getAsString()))
                        .deserialize(obj, ctx, JsonUtils.deserializeClass(obj, "conditions", new LootCondition[0], ctx, LootCondition[].class)));
            }
            return entry;
        } catch (Exception e) {
            return null;
        }
    }

}
