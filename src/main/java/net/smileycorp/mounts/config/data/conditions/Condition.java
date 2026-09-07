package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityLiving;

public interface Condition {
	
	boolean apply(EntityLiving entity);

	interface Deserializer {

		Condition apply(JsonElement obj);

	}

}
