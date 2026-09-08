package net.smileycorp.mounts.config.data.mobs.conditions;

import com.google.gson.JsonElement;
import net.minecraft.world.EnumDifficulty;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.values.Value;

import java.util.Locale;

public class GameDifficultyCondition implements Condition {

	protected final Value<?> difficulty;

	public GameDifficultyCondition(Value<?> difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public boolean ctx(SpawnContext ctx) {
		Comparable<?> value = difficulty.get(ctx);
		return ctx.getWorld().getDifficulty() == (value instanceof String ? EnumDifficulty.valueOf(((String) value).toUpperCase(Locale.US))
				: EnumDifficulty.getDifficultyEnum((Integer) value));
	}

	public static GameDifficultyCondition deserialize(JsonElement json) {
		try {
			Value<?> getter;
			try {
				getter = DataRegistry.readValue(DataType.STRING, json);
			} catch (Exception e) {
				getter = DataRegistry.readValue(DataType.INT, json);
			}
			return new GameDifficultyCondition(getter);
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition game_difficulty", e);
		}
		return null;
	}

}
