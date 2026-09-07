package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.SpawnContext;

public class RandomRangeValue<T extends Number & Comparable<T>> implements Value<T> {

	private final DataType<T> type;
	private final Value<Integer> min, max;

	protected RandomRangeValue(DataType<T> type, Value<Integer> min, Value<Integer> max) {
		this.type = type;
		this.min = min;
		this.max = max;
	}

	@Override
	public T get(SpawnContext ctx) {
		int min = this.min.get(ctx);
		int difference = this.max.get(ctx) - min;
		return type.cast(difference <= 0 ? min : min + ctx.getRandom().nextInt(difference));
	}

	public static <T extends Comparable<T>> Value<T> deserialize(JsonObject obj, DataType<T> type) {
		if (type.isNumber()) {
			try {
				Value<Integer> min = DataRegistry.readValue(DataType.INT, obj.get("min"));
				Value<Integer> max = DataRegistry.readValue(DataType.INT, obj.get("max"));
				return new RandomRangeValue(type, min, max);
			} catch (Exception e) {MountsLogger.logError("incorrect data type for value random_range", e);}
		} else MountsLogger.logError("incorrect data type for value random_range", new ParsingException("data type must be a number"));
		return null;
	}

}
