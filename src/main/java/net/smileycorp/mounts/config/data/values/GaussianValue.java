package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.SpawnContext;

public class GaussianValue implements Value<Double> {

	@Override
	public Double get(SpawnContext ctx) {
		return ctx.getRandom().nextGaussian();
	}

	public static <T extends Comparable<T>> Value<T> deserialize(JsonObject obj, DataType<T> type) {
		if (type == DataType.DOUBLE) return (Value<T>) new GaussianValue();
		MountsLogger.logError("incorrect data type for value gaussian", new ParsingException("data type must be double"));
		return null;
	}

}
