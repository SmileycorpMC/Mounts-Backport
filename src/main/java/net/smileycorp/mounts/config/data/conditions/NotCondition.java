package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityLiving;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;

public class NotCondition implements Condition {

	protected Condition condition;

	public NotCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public boolean apply(EntityLiving entity) {
		return !condition.apply(entity);
	}

	public static NotCondition deserialize(JsonElement json) {
		try {
			return new NotCondition(DataRegistry.readCondition(json.getAsJsonObject()));
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition not", e);
		}
		return null;
	}

}
