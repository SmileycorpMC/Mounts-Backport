package net.smileycorp.mounts.config.data.mobs.values;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;

public class PosValue<T extends Comparable<T>> implements Value<T> {

	private final Value<String> value;
	private final DataType<T> type;

	protected PosValue(Value<String> value, DataType<T> type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public T get(SpawnContext ctx) {
		if (!type.isNumber()) return null;
		EnumFacing.Axis axis = EnumFacing.Axis.byName(value.get(ctx));
		EntityLiving entity = ctx.getEntity();
		if (type == DataType.INT || type == DataType.LONG) {
			BlockPos pos = entity.getPosition();
			switch (axis) {
				case X:
					return type.cast(pos.getX());
				case Y:
					return type.cast(pos.getY());
				default:
					return type.cast(pos.getZ());
			}
		}
		switch (axis) {
			case X:
				return type.cast(entity.posX);
			case Y:
				return type.cast(entity.posY);
			default:
				return type.cast(entity.posZ);
		}
	}

	public static <T extends Comparable<T>> PosValue deserialize(JsonObject obj, DataType<T> type) {
		try {
			if (obj.has("value")) return new PosValue(DataRegistry.readValue(DataType.STRING, obj.get("value")), type);
		} catch (Exception e) {
			MountsLogger.logError("invalid value for get_position", e);
		}
		return null;
	}
	
}
