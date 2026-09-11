package net.smileycorp.mounts.config.data;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.smileycorp.mounts.config.data.mobs.MobDataEntry;
import net.smileycorp.mounts.config.data.mobs.MobDataLoader;
import net.smileycorp.mounts.config.data.mobs.SpawnContext;

import java.util.List;

public class SpawnEggRegistry {

    private static final List<EggEntry> entries = Lists.newArrayList();

    public static void register(EggEntry entry) {
        entries.add(entry);
    }

    public static int getCount() {
        return entries.size();
    }

    public static EggEntry getEntry(int metadata) {
        return metadata >= entries.size() ? null : entries.get(metadata);
    }

    public static abstract class EggEntry {

        private final String name;
        private final EntityEntry entry;

        public EggEntry(String name, EntityEntry entry) {
            this.name = name;
            this.entry = entry;
        }

        public String getName() {
            return name;
        }

        public EntityList.EntityEggInfo getEggInfo() {
            return EntityList.ENTITY_EGGS.get(entry);
        }

        public EntityLiving spawn(World world, double x, double y, double z) {
            Entity entity = entry.newInstance(world);
            if (!(entity instanceof EntityLiving)) return null;
            EntityLiving living = (EntityLiving) entity;
            living.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360f), 0);
            living.rotationYawHead = living.rotationYaw;
            living.renderYawOffset = living.rotationYaw;
            living.setPosition(x, y, z);
            DifficultyInstance difficulty = world.getDifficultyForLocation(entity.getPosition());
            living.onInitialSpawn(difficulty, null);
            world.spawnEntity(living);
            finalizeEntity(living, difficulty);
            living.playLivingSound();
            return living;
        }

        protected abstract void finalizeEntity(EntityLiving entity, DifficultyInstance difficulty);

    }

    public static class MobDataEggEntry extends EggEntry {

        private final String data;

        public MobDataEggEntry(String name, EntityEntry entry, String data) {
            super(name, entry);
            this.data = data;
        }

        @Override
        protected void finalizeEntity(EntityLiving entity, DifficultyInstance difficulty) {
            MobDataEntry data = MobDataLoader.INSTANCE.get(this.data);
            if (this.data == null) return;
            data.applyFunctions(new SpawnContext(entity, difficulty));
        }

    }


}
