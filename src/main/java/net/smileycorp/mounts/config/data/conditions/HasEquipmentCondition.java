package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityLiving;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ItemReference;

public class HasEquipmentCondition implements Condition {

	private final ItemReference item;

	public HasEquipmentCondition(ItemReference item) {
		this.item = item;
	}

	@Override
	public boolean apply(EntityLiving entity) {
		return item.matches(entity);
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
