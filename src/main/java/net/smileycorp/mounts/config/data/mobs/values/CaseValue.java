package net.smileycorp.mounts.config.data.mobs.values;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.data.Pair;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.conditions.Condition;

import java.util.List;

public class CaseValue<T extends Comparable<T>> implements Value<T> {

    private final Value<T> defaultValue;
    private final List<Pair<Value<T>, List<Condition>>> values;

    public CaseValue(Value<T> defaultValue, List<Pair<Value<T>, List<Condition>>> values) {
        this.defaultValue = defaultValue;
        this.values = values;
    }

    @Override
    public T get(SpawnContext ctx) {
        for (Pair<Value<T>, List<Condition>> pair : values) if (DataRegistry.canApply(ctx, pair.getSecond())) return pair.getFirst().get(ctx);
        return defaultValue.get(ctx);
    }
    
    public static <T extends Comparable<T>> CaseValue<T> deserialize(JsonObject obj, DataType<T> type) {
        try {
            Value<T> defaultValue = DataRegistry.readValue(type, obj.get("default"));
            List<Pair<Value<T>, List<Condition>>> values = Lists.newArrayList();
            for (JsonElement element : obj.get("values").getAsJsonArray()) {
                if (!element.isJsonObject()) throw new ParsingException("Value " + element + " must be a json object");
                JsonObject json = element.getAsJsonObject();
                Value<T> value = DataRegistry.readValue(type, json.get("value"));
                List<Condition> conditions = Lists.newArrayList();
                if (obj.has("conditions")) obj.get("conditions").getAsJsonArray().forEach(condition ->
                        conditions.add(DataRegistry.readCondition(condition.getAsJsonObject())));
                values.add(Pair.of(value, conditions));
            }
            return new CaseValue<>(defaultValue, values);
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for value case", e);
        }
        return null;
    }
    
}
