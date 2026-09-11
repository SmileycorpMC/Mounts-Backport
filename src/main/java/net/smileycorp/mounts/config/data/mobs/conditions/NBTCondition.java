package net.smileycorp.mounts.config.data.mobs.conditions;

import com.google.gson.JsonElement;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.values.Value;

public class NBTCondition implements Condition {

	protected final Value<String> nbt;

	public NBTCondition(Value<String> nbt) {
        this.nbt = nbt;
    }

	@Override
	public boolean apply(SpawnContext ctx) {
		try {
			return NBTUtil.areNBTEquals(JsonToNBT.getTagFromJson(nbt.get(ctx)), ctx.getEntity().writeToNBT(new NBTTagCompound()), true);
		} catch (NBTException e) {
            return false;
        }
	}

	public static NBTCondition deserialize(JsonElement json) {
		try {
			return new NBTCondition(DataRegistry.readValue(DataType.STRING, json));
		} catch(Exception e) {
			MountsLogger.logError("Incorrect parameters for condition nbt", e);
		}
		return null;
	}

}
