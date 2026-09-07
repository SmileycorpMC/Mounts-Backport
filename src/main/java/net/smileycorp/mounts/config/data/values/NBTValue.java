package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NBTTagCompound;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.data.NBTExplorer;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.SpawnContext;

public class NBTValue<T extends Comparable<T>> implements Value<T> {

	protected final Value<String> value;
	private final DataType<T> type;
	
	public NBTValue(Value<String> value, DataType<T> type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public T get(SpawnContext ctx) {
		try {
			return new NBTExplorer<>(value.get(entity), type).findValue(entity.getEntity().writeToNBT(new NBTTagCompound()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T extends Comparable<T>> NBTValue<T> deserialize(JsonObject obj, DataType<T> type) {
		try {
			if (obj.has("value")) return new NBTValue<>(DataRegistry.readValue(DataType.STRING, obj.get("value")), type);
		} catch (Exception e) {
			MountsLogger.logError("invalid value for get_nbt", e);
		}
		return null;
	}

}
