package net.smileycorp.mounts.config.data.mounts.conditions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mounts.ItemReference;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;

public class HasEquipmentCondition implements Condition {

	private final ItemReference item;

	public HasEquipmentCondition(ItemReference item) {
		this.item = item;
	}

	@Override
	public boolean ctx(SpawnContext ctx) {
		return item.matches(ctx);
	}

	public static HasEquipmentCondition deserialize(JsonElement json) {
		try {
			return new HasEquipmentCondition(ItemReference.fromJson(json.getAsJsonObject()));
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition has_equipment", e);
		}
		return null;
	}

}
