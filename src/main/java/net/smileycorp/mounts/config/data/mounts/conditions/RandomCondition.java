package net.smileycorp.mounts.config.data.mounts.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mounts.DataRegistry;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;
import net.smileycorp.mounts.config.data.mounts.values.Value;

public class RandomCondition implements Condition {

	protected Value<Double> chance;

	public RandomCondition(Value<Double> chance) {
		this.chance = chance;
	}

	@Override
	public boolean ctx(SpawnContext ctx) {
		return ctx.getRandom().nextFloat() <= chance.get(ctx);
	}

	public static RandomCondition deserialize(JsonElement json) {
		try {
			return new RandomCondition(DataRegistry.readValue(DataType.DOUBLE, json));
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition random", e);
		}
		return null;
	}

}
