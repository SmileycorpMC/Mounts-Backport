package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.config.data.SpawnContext;

public interface Condition {
	
	boolean ctx(SpawnContext ctx);

	interface Deserializer {

		Condition apply(JsonElement obj);

	}

}
