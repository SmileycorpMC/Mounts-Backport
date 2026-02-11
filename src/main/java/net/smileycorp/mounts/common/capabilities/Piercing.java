package net.smileycorp.mounts.common.capabilities;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public interface Piercing {

    @CapabilityInject(Piercing.class)
    Capability<Piercing> CAPABILITY = null;

    boolean canPierce(Entity entity);

    void pierce(Entity entity);

    Collection<Entity> getPiercedEntities();

    void clear();

    class Impl implements Piercing, ICapabilityProvider {

        private final Map<Entity, Long> piercedEntities = Maps.newHashMap();

        @Override
        public boolean canPierce(Entity entity) {
            return !piercedEntities.containsKey(entity) || entity.world.getWorldTime() - piercedEntities.get(entity) < 10;
        }

        @Override
        public void pierce(Entity entity) {
            piercedEntities.put(entity, entity.world.getWorldTime());
        }

        @Override
        public Collection<Entity> getPiercedEntities() {
            return piercedEntities.keySet();
        }

        public void clear() {
            piercedEntities.clear();
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == CAPABILITY ? CAPABILITY.cast(this) : null;
        }

    }

    Capability.IStorage<Piercing> STORAGE = new Capability.IStorage<Piercing>() {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<Piercing> capability, Piercing instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<Piercing> capability, Piercing instance, EnumFacing side, NBTBase nbt) {}

    };

}
