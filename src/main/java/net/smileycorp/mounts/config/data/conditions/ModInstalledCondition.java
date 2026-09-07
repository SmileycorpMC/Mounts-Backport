package net.smileycorp.mounts.config.data.conditions;

import com.google.gson.JsonElement;
import net.minecraftforge.fml.common.Loader;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.SpawnContext;
import net.smileycorp.mounts.config.data.values.Value;

public class ModInstalledCondition implements Condition {
    
    private final Value<String> value;
    
    public ModInstalledCondition(Value<String> value) {
        this.value = value;
    }
    
    @Override
    public boolean ctx(SpawnContext ctx) {
        return Loader.isModLoaded(value.get(entity));
    }
    
    public static ModInstalledCondition deserialize(JsonElement json) {
        try {
            return new ModInstalledCondition(DataRegistry.readValue(DataType.STRING, json));
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for condition mod_installed", e);
        }
        return null;
    }
    
}
