package net.smileycorp.mounts.config.data.values;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.SpawnContext;

import java.util.List;

public class RandomListValue<T extends Comparable<T>> implements Value<T> {

    private final List<Value<T>> values = Lists.newArrayList();

    public RandomListValue(DataType<T> type, JsonArray json) {
        json.forEach(element -> { try {
            values.add(DataRegistry.readValue(type, element));
        } catch (Exception e) {
            MountsLogger.logError("Error loading random value list", e);
        }});
    }

    @Override
    public T get(SpawnContext ctx) {
        return values.get(ctx.getRandom().nextInt(values.size())).get(ctx);
    }

}
