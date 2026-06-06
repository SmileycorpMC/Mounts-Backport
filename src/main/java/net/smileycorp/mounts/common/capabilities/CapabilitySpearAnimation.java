package net.smileycorp.mounts.common.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.smileycorp.mounts.common.Constants;

import javax.annotation.Nullable;

public class CapabilitySpearAnimation
{
    @CapabilityInject(ICapabilityAnimations.class)
    public static Capability<ICapabilityAnimations> MOUNTS_PLAYER_ANIM_CAP;
    public static final ResourceLocation ID = new ResourceLocation(Constants.MODID, "mountsPlayerSpearAnimations");

    /** Timings for the Custom Swing Animations. */
    private static final String CUSTOM_SWING_STARTTICK_TAG = "customSwingStartTime";
    private static final String CUSTOM_SWING_ENDTICK_TAG = "customSwingEndTime";

    private static final String SPEAR_RECOIL_STARTTICK_TAG = "spearRecoilStartTime";

    public interface ICapabilityAnimations
    {
        int getCustomSwingStartTime();
        void setCustomSwingStartTime(int value);

        int getCustomSwingEndTime();
        void setCustomSwingEndTime(int value);

        int getSpearRecoilStartTime();
        void setSpearRecoilStartTime(int value);
    }

    public static class AnimationMethods implements ICapabilityAnimations
    {
        private int customSwingStartTick = 0;
        private int customSwingEndTick = 0;
        private int spearRecoilStartTick = -1000;

        @Override
        public int getCustomSwingStartTime() { return customSwingStartTick; }
        @Override
        public void setCustomSwingStartTime(int value) { customSwingStartTick = value; }

        @Override
        public int getCustomSwingEndTime() { return customSwingEndTick; }
        @Override
        public void setCustomSwingEndTime(int value) { customSwingEndTick = value; }

        @Override
        public int getSpearRecoilStartTime() { return spearRecoilStartTick; }
        @Override
        public void setSpearRecoilStartTime(int value) { spearRecoilStartTick = value; }
    }

    public static class Storage implements Capability.IStorage<ICapabilityAnimations>
    {
        @Override
        public NBTBase writeNBT(Capability<ICapabilityAnimations> capability, ICapabilityAnimations instance, EnumFacing side)
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(CUSTOM_SWING_STARTTICK_TAG, instance.getCustomSwingStartTime());
            compound.setInteger(CUSTOM_SWING_ENDTICK_TAG, instance.getCustomSwingEndTime());
            compound.setInteger(SPEAR_RECOIL_STARTTICK_TAG, instance.getSpearRecoilStartTime());
            return compound;
        }

        @Override
        public void readNBT(Capability<ICapabilityAnimations> capability, ICapabilityAnimations instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setCustomSwingStartTime(compound.getInteger(CUSTOM_SWING_STARTTICK_TAG));
            instance.setCustomSwingEndTime(compound.getInteger(CUSTOM_SWING_ENDTICK_TAG));
            instance.setSpearRecoilStartTime(compound.getInteger(SPEAR_RECOIL_STARTTICK_TAG));
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTBase>
    {
        final Capability<ICapabilityAnimations> capability;
        final EnumFacing facing;
        final ICapabilityAnimations instance;

        public Provider(final ICapabilityAnimations instance, final Capability<ICapabilityAnimations> capability, @Nullable final EnumFacing facing)
        {
            this.instance = instance;
            this.capability = capability;
            this.facing = facing;
        }

        @Override
        public boolean hasCapability(@Nullable final Capability<?> capability, final EnumFacing facing)
        { return capability == getCapability(); }

        @Override
        public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing)
        { return capability == getCapability() ? getCapability().cast(this.instance) : null; }

        final Capability<ICapabilityAnimations> getCapability()
        { return capability; }

        EnumFacing getFacing()
        { return facing; }

        final ICapabilityAnimations getInstance()
        { return instance; }

        @Override
        public NBTBase serializeNBT()
        { return getCapability().writeNBT(getInstance(), getFacing()); }

        @Override
        public void deserializeNBT(NBTBase nbt)
        { getCapability().readNBT(getInstance(), getFacing(), nbt); }
    }
}