package net.smileycorp.mounts.config.data.mobs.functions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;

public interface SpawnFunction {

	void apply(SpawnContext ctx);
	
	interface Deserializer {
		
		SpawnFunction apply(JsonElement element);
		
	}

}