package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.SpawnContext;

public class CastValue<T extends Comparable<T>, V extends Comparable<V>> implements Value<T> {

    private final DataType<T> type;
    private final Value<V> value;

    public CastValue(DataType<T> type, Value<V> value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public T get(SpawnContext entity) {
        return type.cast(value.get(entity));
    }
    
    public static <T extends Comparable<T>, V extends Comparable<V>> CastValue<T, V> deserialize(JsonObject obj, DataType<T> type) {
        try {
            DataType<V> castedType = (DataType<V>) DataType.of(obj.get("type").getAsString());
            Value<V> value = DataRegistry.readValue(castedType, obj.get("value"));
            return new CastValue<>(type, value);
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for value cast", e);
        }
        return null;
    }
    
}
