package net.smileycorp.mounts.config.data.mobs.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;

public interface Condition {
	
	boolean ctx(SpawnContext ctx);

	interface Deserializer {

		Condition apply(JsonElement obj);

	}

}
