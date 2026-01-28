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

public class CapabilitySpearMovement
{
    @CapabilityInject(ICapabilityMountsPlayerInfo.class)
    public static Capability<ICapabilityMountsPlayerInfo> MOUNTS_PLAYER_CAP;
    public static final ResourceLocation ID = new ResourceLocation(Constants.MODID, "mountsPlayerInfo");

    /** A float representing the strength of the Dash. */
    private static final String PLAYER_SPACE_HOLD_TIME = "playerHeldSpaceTime";
    private static final String PLAYER_SPACE_HOLD = "playerHoldingSpace";
    /** The Speed of the player (IDK if this is needed in a Capability??). */

    public interface ICapabilityMountsPlayerInfo
    {
        float getSpaceHeldTime();
        void setSpaceHeldTime(float value);

        boolean getIsSpaceHeld();
        void setIsSpaceHeld(boolean value);

        double getPrevX();
        double getPrevY();
        double getPrevZ();

        void setPrevPos(double x, double y, double z);
    }

    public static class MountsPlayerInfoMethods implements ICapabilityMountsPlayerInfo
    {
        private float mountsSpaceHeldTimeTime = 0;
        private boolean mountsSpaceHeld = false;
        private float mountsPlayerSpeed = 0;
        private double prevX, prevY, prevZ;

        @Override
        public float getSpaceHeldTime()
        { return mountsSpaceHeldTimeTime; }
        @Override
        public void setSpaceHeldTime(float value)
        { mountsSpaceHeldTimeTime = value; }

        @Override
        public boolean getIsSpaceHeld()
        { return mountsSpaceHeld; }
        @Override
        public void setIsSpaceHeld(boolean value)
        { mountsSpaceHeld = value; }

        @Override
        public double getPrevX() {
            return prevX;
        }

        @Override
        public double getPrevY() {
            return prevY;
        }

        @Override
        public double getPrevZ() {
            return prevZ;
        }

        @Override
        public void setPrevPos(double x, double y, double z) {
            prevX = x;
            prevY = y;
            prevZ = z;
        }

    }

    public static class Storage implements Capability.IStorage<ICapabilityMountsPlayerInfo>
    {
        @Override
        public NBTBase writeNBT(Capability<ICapabilityMountsPlayerInfo> capability, ICapabilityMountsPlayerInfo instance, EnumFacing side)
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean(PLAYER_SPACE_HOLD, instance.getIsSpaceHeld());
            compound.setFloat(PLAYER_SPACE_HOLD_TIME, instance.getSpaceHeldTime());
            return compound;
        }

        @Override
        public void readNBT(Capability<ICapabilityMountsPlayerInfo> capability, ICapabilityMountsPlayerInfo instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setIsSpaceHeld(compound.getBoolean(PLAYER_SPACE_HOLD));
            instance.setSpaceHeldTime(compound.getFloat(PLAYER_SPACE_HOLD_TIME));
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTBase>
    {
        final Capability<ICapabilityMountsPlayerInfo> capability;
        final EnumFacing facing;
        final ICapabilityMountsPlayerInfo instance;

        public Provider(final ICapabilityMountsPlayerInfo instance, final Capability<ICapabilityMountsPlayerInfo> capability, @Nullable final EnumFacing facing)
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

        final Capability<ICapabilityMountsPlayerInfo> getCapability()
        { return capability; }

        EnumFacing getFacing()
        { return facing; }

        final ICapabilityMountsPlayerInfo getInstance()
        { return instance; }

        @Override
        public NBTBase serializeNBT()
        { return getCapability().writeNBT(getInstance(), getFacing()); }

        @Override
        public void deserializeNBT(NBTBase nbt)
        { getCapability().readNBT(getInstance(), getFacing(), nbt); }
    }
}
