package net.smileycorp.mounts.config.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.*;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.conditions.*;
import net.smileycorp.mounts.config.data.functions.SpawnFunction;
import net.smileycorp.mounts.config.data.values.*;

import java.util.List;
import java.util.Map;

//ah shit, here we go again
public class DataRegistry {

	private static final Map<String, Value.Deserializer> VALUES = Maps.newHashMap();
	private static final Map<String, Condition.Deserializer> CONDITIONS = Maps.newHashMap();

	public static void init() {
		registerValues();
		registerConditions();
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

	public static void registerConditions() {
		for (LogicalOperation operation : LogicalOperation.values())
			registerCondition(operation.getName(), e -> LogicalCondition.deserialize(operation, e));
		registerCondition("not", NotCondition::deserialize);
		registerCondition("comparison", ComparisonCondition::deserialize);
		registerCondition("mod_installed", ModInstalledCondition::deserialize);
		registerCondition("biome", BiomeCondition::deserialize);
		registerCondition("entity_type", EntityTypeCondition::deserialize);
		registerCondition("regional_difficulty", RegionalDifficultyCondition::deserialize);
		registerCondition("game_difficulty", GameDifficultyCondition::deserialize);
		registerCondition("random", RandomCondition::deserialize);
		registerCondition("has_equipment", HasEquipmentCondition::deserialize);
	}

	public static void registerValue(String name, Value.Deserializer value) {
		VALUES.put(name, value);
	}

	public static void registerCondition(String name, Condition.Deserializer condition) {
		CONDITIONS.put(name, condition);
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

	public static Condition readCondition(JsonObject json) {
		if (json.has("name")) {
			try {
				String name = json.get("name").getAsString();
				Condition.Deserializer deserializer = CONDITIONS.get(name);
				if (deserializer == null) throw new NullPointerException("condition " + name + " is not registered");
				return deserializer.apply(json.get("value"));
			} catch (Exception e) {
				MountsLogger.logError("Failed to read condition " + json, e);
			}
		}
		return null;
	}

	public static boolean canApply(SpawnContext ctx, List<Condition> conditions) {
		for (Condition condition : conditions) if (!condition.ctx(ctx)) return false;
		return true;
	}

}
