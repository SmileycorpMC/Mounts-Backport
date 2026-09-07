package net.smileycorp.mounts.config.data.mounts.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;

public interface Condition {
	
	boolean ctx(SpawnContext ctx);

	interface Deserializer {

		Condition apply(JsonElement obj);

	}

}
