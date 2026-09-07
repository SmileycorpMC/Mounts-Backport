package net.smileycorp.mounts.config.data.mounts.functions;

import com.google.gson.JsonElement;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mounts.DataRegistry;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;
import net.smileycorp.mounts.config.data.mounts.values.Value;

public class SetPosYFunction implements SpawnFunction {

    private final Value<Double> value;

    public SetPosYFunction(Value<Double> value) {
        this.value = value;
    }

    @Override
    public void apply(SpawnContext ctx) {
        ctx.getEntity().posY = value.get(ctx);
    }
    
    public static SetPosYFunction deserialize(JsonElement json) {
        try {
            return new SetPosYFunction(DataRegistry.readValue(DataType.DOUBLE, json));
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function set_y", e);
        }
        return null;
    }
    
}
