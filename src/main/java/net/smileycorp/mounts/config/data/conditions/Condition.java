package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.config.data.SpawnContext;

public interface Condition {
	
	boolean ctx(SpawnContext entity);

	interface Deserializer {

		Condition apply(JsonElement obj);

	}

}
