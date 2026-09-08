package net.smileycorp.mounts.config.data.mobs.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.data.Pair;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;
import net.smileycorp.mounts.config.data.mobs.conditions.Condition;
import net.smileycorp.mounts.config.data.mobs.values.Value;

import java.util.List;

public class SpawnEntityFunction implements SpawnFunction {

    private final Value<String> type;
    private final List<Pair<SpawnFunction, List<Condition>>> functions;

    public SpawnEntityFunction(Value<String> value, List<Pair<SpawnFunction, List<Condition>>> functions) {
        this.type = value;
        this.functions = functions;
    }
    
    @Override
    public void apply(SpawnContext ctx) {
        try {
            EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(type.get(ctx)));
            if (!EntityLiving.class.isAssignableFrom(entry.getEntityClass())) return;
            World world = ctx.getWorld();
            EntityLiving entity = ctx.getEntity();
            EntityLiving newEntity = (EntityLiving) entry.newInstance(world);
            entity.setPosition(entity.posX, entity.posY, entity.posZ);
            newEntity.onInitialSpawn(ctx.getDifficulty(), null);
            applyFunctions(ctx, newEntity);
            newEntity.world.spawnEntity(newEntity);
        } catch (Exception e) {}
    }

    protected void applyFunctions(SpawnContext ctx, EntityLiving entity) {
        SpawnContext newContext = new SpawnContext(entity, ctx.getDifficulty());
        for (Pair<SpawnFunction, List<Condition>> pair : functions) {
            if (DataRegistry.canApply(ctx, pair.getSecond())) pair.getFirst().apply(newContext);
            if (ctx.isBroken()) break;
        }
    }
    
    public static SpawnEntityFunction deserialize(JsonElement json) {
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
            return new SpawnEntityFunction(DataRegistry.readValue(DataType.STRING, json), functions);
        } catch(Exception e) {
            MountsLogger.logError("Incorrect parameters for function spawn_entity", e);
        }
        return null;
    }
    
}
