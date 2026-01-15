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

    /** Whenever a player preforms a missed left-click. */
    private static final String PLAYER_LEFT_CLICK = "playerLeftClick";
    private static final String PLAYER_SPACE_HOLD = "playerHoldingSpace";
    /** The Speed of the player (IDK if this is needed in a Capability??). */
    private static final String PLAYER_SPEED = "playerSpeed";

    public interface ICapabilityMountsPlayerInfo
    {
        boolean getPlayerLeftClick();
        void setPlayerLeftClick(boolean value);

        boolean getPlayerJump();
        void setPlayerJump(boolean value);

        float getPlayerSpeed();
        void setPlayerSpeed(float value);
    }

    public static class MountsPlayerInfoMethods implements ICapabilityMountsPlayerInfo
    {
        private boolean mountsPlayerLeftClick = false;
        private boolean mountsPlayerJump = false;
        private float mountsPlayerSpeed = 0;

        @Override
        public boolean getPlayerLeftClick()
        { return mountsPlayerLeftClick; }
        @Override
        public void setPlayerLeftClick(boolean value)
        { mountsPlayerLeftClick = value; }

        @Override
        public boolean getPlayerJump()
        { return mountsPlayerJump; }
        @Override
        public void setPlayerJump(boolean value)
        { mountsPlayerJump = value; }

        @Override
        public float getPlayerSpeed()
        { return mountsPlayerSpeed; }
        @Override
        public void setPlayerSpeed(float value)
        { mountsPlayerSpeed = value; }
    }

    public static class Storage implements Capability.IStorage<ICapabilityMountsPlayerInfo>
    {
        @Override
        public NBTBase writeNBT(Capability<ICapabilityMountsPlayerInfo> capability, ICapabilityMountsPlayerInfo instance, EnumFacing side)
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean(PLAYER_LEFT_CLICK, instance.getPlayerLeftClick());
            compound.setBoolean(PLAYER_SPACE_HOLD, instance.getPlayerJump());
            compound.setFloat(PLAYER_SPEED, instance.getPlayerSpeed());
            return compound;
        }

        @Override
        public void readNBT(Capability<ICapabilityMountsPlayerInfo> capability, ICapabilityMountsPlayerInfo instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setPlayerLeftClick(compound.getBoolean(PLAYER_LEFT_CLICK));
            instance.setPlayerJump(compound.getBoolean(PLAYER_SPACE_HOLD));
            instance.setPlayerSpeed(compound.getFloat(PLAYER_SPEED));
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
