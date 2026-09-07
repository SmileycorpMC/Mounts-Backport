package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.SpawnContext;
import net.smileycorp.mounts.config.data.values.Value;

public class RegionalDifficultyCondition implements Condition {

	protected Value<Double> difficulty;

	public RegionalDifficultyCondition(Value<Double> difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public boolean ctx(SpawnContext ctx) {
		return entity.getDifficulty().getClampedAdditionalDifficulty() > difficulty.get(entity);
	}

	public static RegionalDifficultyCondition deserialize(JsonElement json) {
		try {
			return new RegionalDifficultyCondition(DataRegistry.readValue(DataType.DOUBLE, json));
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition regional_difficulty", e);
		}
		return null;
	}

}
