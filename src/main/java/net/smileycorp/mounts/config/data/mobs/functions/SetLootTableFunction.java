package net.smileycorp.mounts.config.data.mobs.functions;

import com.google.gson.JsonElement;
import net.minecraft.nbt.NBTTagCompound;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.values.Value;

public class SetLootTableFunction implements SpawnFunction {
    
    private final Value<String> value;
    
    public SetLootTableFunction(Value<String> value) {
        this.value = value;
    }
    
    @Override
    public void apply(SpawnContext ctx) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("DeathLootTable", value.get(ctx));
        ctx.getEntity().readEntityFromNBT(tag);
    }
    
    public static SetLootTableFunction deserialize(JsonElement json) {
        try {
            return new SetLootTableFunction(DataRegistry.readValue(DataType.STRING, json));
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function set_loot_table", e);
        }
        return null;
    }
    
}
