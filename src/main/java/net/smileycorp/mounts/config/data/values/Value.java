package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.config.data.SpawnContext;

public interface Value<T extends Comparable<T>> {

    T get(SpawnContext entity);

    interface Deserializer {

        <T extends Comparable<T>> Value<T> apply(JsonObject obj, DataType<T> type);

    }

}
