package net.smileycorp.mounts.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearAnimation;

/** This is used so the Server can tell Clients when the Riptide Capability is altered, so animations are displayed. */
public class SpearSwingAnimMessage implements IMessage
{
    private int entityId;
    private int duration;

    public SpearSwingAnimMessage() {}

    public SpearSwingAnimMessage(int entityId, int durationIn)
    {
        this.entityId = entityId;
        this.duration = durationIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityId = buf.readInt();
        duration = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityId);
        buf.writeInt(duration);
    }

    public static class Handler implements IMessageHandler<SpearSwingAnimMessage, IMessage>
    {
        @Override
        public IMessage onMessage(SpearSwingAnimMessage message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                Entity player = Minecraft.getMinecraft().world.getEntityByID(message.entityId);

                if(player.hasCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null))
                {
                    CapabilitySpearAnimation.ICapabilityAnimations riptide = player.getCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null);
                    riptide.setCustomSwingStartTime(player.ticksExisted);
                    riptide.setCustomSwingEndTime(player.ticksExisted + message.duration);
                }
                //Helper.setRiptideCapability(player, message.isActive);
            });
            return null;
        }
    }
}