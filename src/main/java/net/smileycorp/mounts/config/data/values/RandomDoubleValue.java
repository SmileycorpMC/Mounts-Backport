package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.SpawnContext;

public class RandomDoubleValue implements Value<Double> {

	@Override
	public Double get(SpawnContext ctx) {
		return entity.getRandom().nextDouble();
	}

	public static <T extends Comparable<T>> Value<T> deserialize(JsonObject obj, DataType<T> type) {
		if (type == DataType.DOUBLE) return (Value<T>) new RandomDoubleValue();
		MountsLogger.logError("incorrect data type for value random_double", new ParsingException("data type must be double"));
		return null;
	}

}
