package net.smileycorp.mounts.config.data.mobs.values;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.recipe.WeightedOutputs;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;

import java.util.Map;

public class WeightedRandomValue<T extends Comparable<T>> implements Value<T> {
    
    private final WeightedOutputs<Value<T>> outputs;
    
    public WeightedRandomValue(WeightedOutputs<Value<T>> outputs) {
        this.outputs = outputs;
    }

    @Override
    public T get(SpawnContext ctx) {
        return outputs.getResult(ctx.getRandom()).get(ctx);
    }
    
    public static <T extends Comparable<T>> WeightedRandomValue<T> deserialize(JsonObject json, DataType<T> type) {
        Map<Value<T>, Integer> values = Maps.newHashMap();
        for (JsonElement element : json.get("value").getAsJsonArray()) {
            try {
                JsonObject entry = element.getAsJsonObject();
                Value<T> getter = DataRegistry.readValue(type, entry.get("value"));
                if (getter != null) values.put(getter, entry.get("weight").getAsInt());
            } catch (Exception e) {
                MountsLogger.logError("invalid entry for " + element + " for weighted_random", e);
            }
        }
        return new WeightedRandomValue<>(new WeightedOutputs<>(values));
    }
    
}
