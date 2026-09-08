package net.smileycorp.mounts.config.data.mobs.functions;

import com.google.gson.JsonElement;
import net.minecraft.nbt.NBTTagCompound;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.values.Value;

public class SetNBTFunction implements SpawnFunction {
    
    private final Value<String> value;
    
    public SetNBTFunction(Value<String> value) {
        this.value = value;
    }
    
    @Override
    public void apply(SpawnContext ctx) {
        String str = value.get(ctx);
        try {
            NBTTagCompound nbt = DataRegistry.parseNBT(ctx.getEntity().toString(), str);
            ctx.getEntity().readFromNBT(nbt);
        } catch (Exception e) {
            MountsLogger.logError("Failed loading nbt " + str + " for entity " + ctx.getEntity(), e);
        }
    }
    
    public static SetNBTFunction deserialize(JsonElement json) {
        try {
            return new SetNBTFunction(DataRegistry.readValue(DataType.STRING, json));
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function set_nbt", e);
        }
        return null;
    }
    
}
