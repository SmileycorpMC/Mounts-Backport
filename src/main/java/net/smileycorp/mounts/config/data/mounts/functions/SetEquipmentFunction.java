package net.smileycorp.mounts.config.data.mounts.functions;

import com.google.gson.JsonElement;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mounts.ItemReference;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;

public class SetEquipmentFunction implements SpawnFunction {

    private final ItemReference item;

    public SetEquipmentFunction(ItemReference item) {
        this.item = item;
    }
    
    @Override
    public void apply(SpawnContext ctx) {
       item.setItem(ctx);
    }
    
    public static SetEquipmentFunction deserialize(JsonElement json) {
        try {
            return new SetEquipmentFunction(ItemReference.fromJson(json.getAsJsonObject()));
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function set_equipment", e);
        }
        return null;
    }
    
}
