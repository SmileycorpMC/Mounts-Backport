package net.smileycorp.mounts.config.data.mobs.conditions;

import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.values.Value;

public class EntityTypeCondition implements Condition {

	protected Value<String> getter;

	public EntityTypeCondition(Value<String> getter) {
		this.getter = getter;
	}

	@Override
	public boolean apply(SpawnContext ctx) {
		EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(getter.get(ctx)));
		return entry != null && entry.getEntityClass() == ctx.getEntity().getClass();
	}

	public static EntityTypeCondition deserialize(JsonElement json) {
		try {
			return new EntityTypeCondition(DataRegistry.readValue(DataType.STRING, json));
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition entity_type", e);
		}
		return null;
	}

}
