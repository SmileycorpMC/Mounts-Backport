package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.ComparableOperation;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.SpawnContext;
import net.smileycorp.mounts.config.data.values.Value;

public class ComparisonCondition<T extends Comparable<T>> implements Condition {

	protected final Value<T> value1;
	protected final ComparableOperation operation;
	protected final Value<T> value2;

	private ComparisonCondition(Value<T> value1, ComparableOperation operation, Value<T> value2) {
		this.value1 = value1;
		this.operation = operation;
		this.value2 = value2;
	}

	@Override
	public boolean ctx(SpawnContext ctx) {
		return operation.apply(value1.get(entity), value2.get(entity));
	}

	public static <T extends Comparable<T>> ComparisonCondition<T> deserialize(JsonElement json) {
		try {
			JsonObject obj = json.getAsJsonObject();
			DataType<T> type = (DataType<T>) DataType.of(obj.get("type").getAsString());
			ComparableOperation operation = ComparableOperation.of(obj.get("operation").getAsString());
			Value<T> value1 = DataRegistry.readValue(type, obj.get("value1"));
			Value<T> value2 = DataRegistry.readValue(type, obj.get("value2"));
			return new ComparisonCondition<>(value1, operation, value2);
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition comparison", e);
		}
		return null;
	}

}
