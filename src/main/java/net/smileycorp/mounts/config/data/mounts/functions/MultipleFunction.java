package net.smileycorp.mounts.config.data.mounts.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.Pair;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mounts.DataRegistry;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;
import net.smileycorp.mounts.config.data.mounts.conditions.Condition;

import java.util.List;

public class MultipleFunction implements SpawnFunction {

    private final List<Pair<SpawnFunction, List<Condition>>> functions;

    public MultipleFunction(List<Pair<SpawnFunction, List<Condition>>> functions) {
        this.functions = functions;
    }

    @Override
    public void apply(SpawnContext ctx) {
        for (Pair<SpawnFunction, List<Condition>> pair : functions) {
            if (DataRegistry.canApply(ctx, pair.getSecond())) pair.getFirst().apply(ctx);
            if (ctx.isBroken()) break;
        }
        ctx.resetState();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < functions.size(); i++) {
            builder.append(functions.get(i).toString());
            if (i < functions.size() - 1) builder.append(" && ");
        }
        return super.toString() + "[" + builder + "]";
    }

    public static MultipleFunction deserialize(JsonElement json) {
        try {
            List<Pair<SpawnFunction, List<Condition>>> functions = Lists.newArrayList();
            for (JsonElement element : json.getAsJsonArray()) {
                JsonObject obj = element.getAsJsonObject();
                SpawnFunction function =  DataRegistry.readFunction(obj);
                List<Condition> conditions = Lists.newArrayList();
                if (obj.has("conditions")) obj.get("conditions").getAsJsonArray().forEach(condition ->
                        conditions.add(DataRegistry.readCondition(condition.getAsJsonObject())));
                functions.add(Pair.of(function, conditions));
            }
            return new MultipleFunction(functions);
        } catch (Exception e) {
            MountsLogger.logError("Error reading function multiple", e);
            return null;
        }
    }

}
