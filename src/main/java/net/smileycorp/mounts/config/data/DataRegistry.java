package net.smileycorp.mounts.config.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.BinaryOperation;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.data.UnaryOperation;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.values.*;

import java.util.Map;

//ah shit, here we go again
public class DataRegistry {

	private static final Map<String, Value.Deserializer> VALUES = Maps.newHashMap();

	public static void init() {
		registerValues();
	}

	private static void registerValues() {
		registerValue("cast", CaseValue::deserialize);
		registerValue("case", CaseValue::deserialize);
		registerValue("weighted_random", WeightedRandomValue::deserialize);
		UnaryOperation.values().forEach(operation -> registerValue(operation.getName(),
				UnaryOperationValue.of(operation)::deserialize));
		BinaryOperation.values().forEach(operation -> registerValue(operation.getName(),
				BinaryOperationValue.of(operation)::deserialize));
		registerValue("random_range", RandomRangeValue::deserialize);
		registerValue("random_float", RandomFloatValue::deserialize);
		registerValue("random_double", RandomDoubleValue::deserialize);
		registerValue("gaussian", GaussianValue::deserialize);
		registerValue("get_nbt", NBTValue::deserialize);
		registerValue("get_pos", PosValue::deserialize);
		registerValue("regional_difficulty", RegionalDifficultyValue::deserialize);
	}

	public static void registerValue(String name, Value.Deserializer deserializer) {
		VALUES.put(name, deserializer);
	}

	public static <T extends Comparable<T>> Value<T> readValue(DataType<T> type, JsonElement json) throws Exception {
		if (json instanceof JsonNull) throw new ParsingException("No value present");
		if (json.isJsonObject()) {
			JsonObject obj = json.getAsJsonObject();
			try {
				String name = obj.get("name").getAsString();
				Value.Deserializer deserializer = VALUES.get(name);
				if (deserializer == null) throw new NullPointerException("value " + name + " is not registered");
				return deserializer.apply(obj, type);
			} catch (Exception e) {
				MountsLogger.logError("Failed to read value " + obj, e);
			}
			return null;
		} else if (json.isJsonArray()) {
			return new RandomListValue(type, json.getAsJsonArray());
		}
		T value = type.readFromJson(json);
		return e -> value;
	}

}
