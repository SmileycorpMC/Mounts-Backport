package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.SpawnContext;

public class RegionalDifficultyValue <T extends Number & Comparable<T>> implements Value<T> {

	private final DataType<T> type;

	public RegionalDifficultyValue(DataType<T> type) {
		this.type = type;
	}

	@Override
	public T get(SpawnContext ctx) {
		return type.cast(entity.getDifficulty().getClampedAdditionalDifficulty());
	}

	public static <T extends Comparable<T>> Value<T> deserialize(JsonObject obj, DataType<T> type) {
		if (type == DataType.FLOAT || type == DataType.DOUBLE) return new RegionalDifficultyValue(type);
		MountsLogger.logError("incorrect data type for value regional_difficulty", new ParsingException("data type must be float or double"));
		return null;
	}

}
