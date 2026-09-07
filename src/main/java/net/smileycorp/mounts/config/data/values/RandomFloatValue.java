package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;

public class RandomFloatValue<T extends Number & Comparable<T>> implements Value<T> {

	private final DataType<T> type;

	protected RandomFloatValue(DataType<T> type) {
		this.type = type;
	}

	@Override
	public T get(EntityLiving entity) {
		return type.cast(entity.getRNG().nextFloat());
	}

	public static <T extends Comparable<T>> Value<T> deserialize(JsonObject obj, DataType<T> type) {
		if (type == DataType.FLOAT || type == DataType.DOUBLE) return new RandomFloatValue(type);
		MountsLogger.logError("incorrect data type for value random_float", new ParsingException("data type must be float or double"));
		return null;
	}

}
