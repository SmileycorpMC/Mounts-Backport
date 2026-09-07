package net.smileycorp.mounts.config.data.mounts.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;

public interface Value<T extends Comparable<T>> {

    T get(SpawnContext ctx);

    interface Deserializer {

        <T extends Comparable<T>> Value<T> apply(JsonObject obj, DataType<T> type);

    }

}
