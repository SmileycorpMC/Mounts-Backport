package net.smileycorp.mounts.config.data.mounts.functions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;

public interface SpawnFunction {

	void apply(SpawnContext ctx);
	
	interface Deserializer {
		
		SpawnFunction apply(JsonElement element);
		
	}

}