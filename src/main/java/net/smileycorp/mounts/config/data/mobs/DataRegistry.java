package net.smileycorp.mounts.config.data.mobs;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.smileycorp.atlas.api.data.BinaryOperation;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.data.LogicalOperation;
import net.smileycorp.atlas.api.data.UnaryOperation;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.mobs.conditions.*;
import net.smileycorp.mounts.config.data.mobs.functions.*;
import net.smileycorp.mounts.config.data.mobs.values.*;

import java.util.List;
import java.util.Map;

//ah shit, here we go again
public class DataRegistry {

	private static final Map<String, Value.Deserializer> VALUES = Maps.newHashMap();
	private static final Map<String, Condition.Deserializer> CONDITIONS = Maps.newHashMap();
	private static final Map<String, SpawnFunction.Deserializer> FUNCTIONS = Maps.newHashMap();

	public static void init() {
		registerValues();
		registerConditions();
		registerFunctions();
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

	public static void registerFunctions() {
		registerFunction("multiple", MultipleFunction::deserialize);
		registerFunction("random", RandomFunction::deserialize);
		registerFunction("weighted_random", WeightedRandomFunction::deserialize);
		registerFunction("break", json -> SpawnContext::breakScript);
		registerFunction("return", json -> SpawnContext::returnScript);
		registerFunction("spawn_entity", SpawnEntityFunction::deserialize);
		registerFunction("add_mount", AddMountFunction::deserialize);
		registerFunction("add_passenger", AddPassengerFunction::deserialize);
		registerFunction("set_equipment", SetEquipmentFunction::deserialize);
		registerFunction("enchant_equipment", EnchantEquipmentFunction::deserialize);
		registerFunction("set_x", SetPosXFunction::deserialize);
		registerFunction("set_y", SetPosYFunction::deserialize);
		registerFunction("set_z", SetPosZFunction::deserialize);
		registerFunction("set_nbt", SetNBTFunction::deserialize);
	}

	public static void registerValue(String name, Value.Deserializer value) {
		VALUES.put(name, value);
	}

	public static void registerCondition(String name, Condition.Deserializer condition) {
		CONDITIONS.put(name, condition);
	}

	public static void registerFunction(String name, SpawnFunction.Deserializer function) {
		FUNCTIONS.put(name, function);
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

	public static SpawnFunction readFunction(JsonObject json) throws Exception {
		if (json.has("function")) {
			try {
				String name = json.get("function").getAsString();
				SpawnFunction.Deserializer deserializer = FUNCTIONS.get(name);
				if (deserializer == null) throw new NullPointerException("condition " + name + " is not registered");
				return deserializer.apply(json.get("value"));
			} catch (Exception e) {
				MountsLogger.logError("Failed to read function " + json, e);
			}
		}
		return null;
	}

	public static boolean canApply(SpawnContext ctx, List<Condition> conditions) {
		for (Condition condition : conditions) if (!condition.apply(ctx)) return false;
		return true;
	}

	public static NBTTagCompound parseNBT(String name, String nbtstring) {
		try {
			return JsonToNBT.getTagFromJson(nbtstring);
		} catch (Exception e) {
			MountsLogger.logError("Error parsing nbt for " + name + " " + e.getMessage(), e);
		}
		return null;
	}

}
