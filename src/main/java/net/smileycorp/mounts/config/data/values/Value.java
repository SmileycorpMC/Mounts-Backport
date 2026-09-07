package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.smileycorp.atlas.api.data.DataType;

public interface Value<T extends Comparable<T>> {

    T get(EntityLiving entity);

    interface Deserializer {

        <T extends Comparable<T>> Value<T> apply(JsonObject obj, DataType<T> type);

    }

}
