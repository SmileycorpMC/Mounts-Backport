package net.smileycorp.mounts.config.data.mobs.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.data.Pair;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.conditions.Condition;
import net.smileycorp.mounts.config.data.mobs.values.Value;

import java.util.List;

public class AddPassengerFunction extends SpawnEntityFunction {

    public AddPassengerFunction(Value<String> value, List<Pair<SpawnFunction, List<Condition>>> functions) {
        super(value, functions);
    }

    protected void applyFunctions(SpawnContext ctx, EntityLiving entity) {
       super.applyFunctions(ctx, entity);
       entity.startRiding(ctx.getEntity());
    }
    
    public static AddPassengerFunction deserialize(JsonElement json) {
        try {
            JsonObject obj = json.getAsJsonObject();
            List<Pair<SpawnFunction, List<Condition>>> functions = Lists.newArrayList();
            for (JsonElement element : obj.get("functions").getAsJsonArray()) {
                JsonObject obj1 = element.getAsJsonObject();
                SpawnFunction function =  DataRegistry.readFunction(obj1);
                List<Condition> conditions = Lists.newArrayList();
                if (obj1.has("conditions")) obj1.get("conditions").getAsJsonArray().forEach(condition ->
                        conditions.add(DataRegistry.readCondition(condition.getAsJsonObject())));
                functions.add(Pair.of(function, conditions));
            }
            return new AddPassengerFunction(DataRegistry.readValue(DataType.STRING, obj.get("type")), functions);
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function add_mount", e);
        }
        return null;
    }
    
}
