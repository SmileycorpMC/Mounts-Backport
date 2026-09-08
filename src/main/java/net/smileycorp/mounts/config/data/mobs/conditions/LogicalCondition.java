package net.smileycorp.mounts.config.data.mobs.conditions;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import net.smileycorp.atlas.api.data.LogicalOperation;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;

import java.util.List;

public class LogicalCondition implements Condition {

	protected final LogicalOperation operation;
	protected final Condition[] conditions;

	private LogicalCondition(LogicalOperation operation, Condition... conditions) {
		this.operation = operation;
		this.conditions = conditions;
	}

	@Override
	public boolean ctx(SpawnContext ctx) {
		boolean result = false;
		for (Condition condition : conditions) result = operation.apply(result, condition.ctx(ctx));
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < conditions.length; i++) {
			builder.append(conditions[i].toString());
			if (i < conditions.length-1) builder.append(" " + operation.getSymbol() + " ");
		}
		return super.toString() + "[" + builder + "]";
	}

	public static LogicalCondition deserialize(LogicalOperation operation, JsonElement json) {
		try {
			List<Condition> conditions = Lists.newArrayList();
			for (JsonElement element : json.getAsJsonArray()) {
				try {
					conditions.add(DataRegistry.readCondition(element.getAsJsonObject()));
				} catch(Exception e) {
					MountsLogger.logError("Failed to read condition of logical " + element, e);
				}
			}
			return new LogicalCondition(operation, conditions.toArray(new Condition[conditions.size()]));
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition " + operation.getName(), e);
		}
		return null;
	}

}
