package net.smileycorp.mounts.config.data.functions;

import com.google.gson.JsonElement;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.SpawnContext;
import net.smileycorp.mounts.config.data.values.Value;

public class SetPosXFunction implements SpawnFunction {
    
    private final Value<Double> value;
    
    public SetPosXFunction(Value<Double> value) {
        this.value = value;
    }

    @Override
    public void apply(SpawnContext ctx) {
        ctx.getEntity().posX = value.get(ctx);
    }
    
    public static SetPosXFunction deserialize(JsonElement json) {
        try {
            return new SetPosXFunction(DataRegistry.readValue(DataType.DOUBLE, json));
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function set_x", e);
        }
        return null;
    }
    
}
