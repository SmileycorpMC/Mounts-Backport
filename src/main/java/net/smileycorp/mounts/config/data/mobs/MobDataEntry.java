package net.smileycorp.mounts.config.data.mobs;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.smileycorp.atlas.api.data.Pair;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.SpawnEggRegistry;
import net.smileycorp.mounts.config.data.mobs.conditions.Condition;
import net.smileycorp.mounts.config.data.mobs.functions.SpawnFunction;

import java.util.List;

public class MobDataEntry {

    private final String name;
    private final List<EntityEntry> entities;
    private final float spawnChance;
    private final boolean spawnEgg;
    private final List<Pair<SpawnFunction, List<Condition>>> functions;
    private final List<Condition> conditions;

    private MobDataEntry(Builder builder) {
        name = builder.name;
        entities = builder.entities;
        spawnChance = builder.spawnChance;
        spawnEgg = builder.spawnEgg;
        functions = builder.functions;
        conditions = builder.conditions;
    }

    public String getName() {
        return name;
    }

    public boolean hasSpawnEgg() {
        return spawnEgg;
    }

    public boolean canApply(EntityLiving entity) {
        for (EntityEntry entry : entities) if (entity.getClass() == entry.getEntityClass()) return true;
        return false;
    }

    public boolean canSpawn(SpawnContext ctx) {
        return spawnChance > 0 && DataRegistry.canApply(ctx, conditions) && (spawnChance == 1 || ctx.getRandom().nextFloat() <= spawnChance);
    }

    public void applyFunctions(SpawnContext ctx) {
        for (Pair<SpawnFunction, List<Condition>> pair : functions) try {
            if (!DataRegistry.canApply(ctx, pair.getSecond())) continue;
            pair.getFirst().apply(ctx);
        } catch (Exception e) {
            MountsLogger.logError("Failed to apply functions " + pair, e);
        }
    }

    public void registerSpawnEggs() {
        String langKey = "mount." + name;
        for (EntityEntry entity : entities) SpawnEggRegistry.register(new SpawnEggRegistry.MobDataEggEntry(entities.size() == 1 ? langKey :
                    langKey + "." + entity.getRegistryName().toString().replace(":", "."), entity, this.name));
    }

    public static class Builder {

        private final String name;
        private final List<EntityEntry> entities;
        private float spawnChance = 0;
        private boolean spawnEgg = true;
        private final List<Pair<SpawnFunction, List<Condition>>> functions = Lists.newArrayList();
        private final List<Condition> conditions = Lists.newArrayList();

        private Builder(String name, List<EntityEntry>  entities) {
            this.name = name;
            this.entities = entities;
        }

        public Builder spawnChance(float spawnChance) {
            this.spawnChance = MathHelper.clamp(spawnChance, 0, 1);
            return this;
        }

        public Builder spawnEgg(boolean spawnEgg) {
            this.spawnEgg = spawnEgg;
            return this;
        }

        public Builder function(SpawnFunction function, List<Condition> conditions) {
            functions.add(Pair.of(function, conditions));
            return this;
        }

        public Builder condition(Condition condition) {
            conditions.add(condition);
            return this;
        }

        public MobDataEntry build() {
            return new MobDataEntry(this);
        }

        public static Builder of(String name, List<EntityEntry> entities) {
            return new Builder(name, entities);
        }

    }

}
