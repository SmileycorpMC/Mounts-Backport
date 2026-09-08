package net.smileycorp.mounts.config.data.mobs.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;

public interface Value<T extends Comparable<T>> {

    T get(SpawnContext ctx);

    interface Deserializer {

        <T extends Comparable<T>> Value<T> apply(JsonObject obj, DataType<T> type);

    }

}
