package net.smileycorp.mounts.config.data.mounts.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.Pair;
import net.smileycorp.atlas.api.recipe.WeightedOutputs;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mounts.DataRegistry;
import net.smileycorp.mounts.config.data.mounts.SpawnContext;
import net.smileycorp.mounts.config.data.mounts.conditions.Condition;

import java.util.List;
import java.util.stream.Collectors;

public class WeightedRandomFunction implements SpawnFunction {

    private final List<Pair<Pair<SpawnFunction, Integer>, List<Condition>>> functions;

    public WeightedRandomFunction(List<Pair<Pair<SpawnFunction, Integer>, List<Condition>>> functions) {
        this.functions = functions;
    }
    
    @Override
    public void apply(SpawnContext ctx) {
        WeightedOutputs<SpawnFunction> functions = new WeightedOutputs<>(1, this.functions.stream().
                filter(pair -> DataRegistry.canApply(ctx, pair.getSecond()))
                .map(pair -> pair.getFirst().toEntry()).collect(Collectors.toList()));
        if (functions.isEmpty()) return;
        functions.getResults(ctx.getRandom()).forEach(func -> func.apply(ctx));
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

    public static WeightedRandomFunction deserialize(JsonElement json) {
        try {
            List<Pair<Pair<SpawnFunction, Integer>, List<Condition>>> functions = Lists.newArrayList();
            for (JsonElement element : json.getAsJsonArray()) {
                JsonObject obj = element.getAsJsonObject();
                SpawnFunction function = DataRegistry.readFunction(obj);
                List<Condition> conditions = Lists.newArrayList();
                if (obj.has("conditions")) obj.get("conditions").getAsJsonArray().forEach(condition ->
                        conditions.add(DataRegistry.readCondition(condition.getAsJsonObject())));
                functions.add(Pair.of(Pair.of(function, obj.has("weight") ? obj.get("weight").getAsInt() : 0), conditions));
            }
            return new WeightedRandomFunction(functions);
        } catch (Exception e) {
            MountsLogger.logError("Error reading weighted_random function", e);
            return null;
        }
    }

}
