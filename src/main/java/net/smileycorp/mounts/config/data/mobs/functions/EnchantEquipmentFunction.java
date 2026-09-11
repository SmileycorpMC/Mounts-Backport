package net.smileycorp.mounts.config.data.mobs.functions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.util.Func;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.values.Value;

public class EnchantEquipmentFunction implements SpawnFunction {

    private final Value<String> slot;
    private final Value<Integer> level;
    private final Value<Boolean> allowTreasure;

    public EnchantEquipmentFunction(Value<String> slot, Value<Integer> level, Value<Boolean> allowTreasure) {
        this.slot = slot;
        this.level = level;
        this.allowTreasure = allowTreasure;
    }
    
    @Override
    public void apply(SpawnContext ctx) {
       try {
           ItemStack stack = ctx.getEntity().getItemStackFromSlot(EntityEquipmentSlot.fromString(slot.get(ctx)));
           EnchantmentHelper.addRandomEnchantment(ctx.getRandom(), stack, level.get(ctx), allowTreasure.get(ctx));
       } catch (Exception e) {}
    }
    
    public static EnchantEquipmentFunction deserialize(JsonElement json) {
        try {
            JsonObject obj = json.getAsJsonObject();
            Value<Boolean> allowTreasure = obj.has("allow_treasure") ?
                    DataRegistry.readValue(DataType.BOOLEAN, obj.get("allow_treasure")) : Func::False;
            return new EnchantEquipmentFunction(DataRegistry.readValue(DataType.STRING, obj.get("slot")),
                    DataRegistry.readValue(DataType.INT, obj.get("level")), allowTreasure);
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function set_equipment", e);
        }
        return null;
    }
    
}
