package net.smileycorp.mounts.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearAnimation;

public class SpearAnimSwingServerMessage implements IMessage
{
    private int entityId;
    private int duration;

    public SpearAnimSwingServerMessage() {}

    public SpearAnimSwingServerMessage(int entityId, int durationIn)
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

    public static class Handler implements IMessageHandler<SpearAnimSwingServerMessage, IMessage>
    {
        @Override
        public IMessage onMessage(SpearAnimSwingServerMessage message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;

            if(player.hasCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null))
            {
                CapabilitySpearAnimation.ICapabilityAnimations anim = player.getCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null);
                anim.setCustomSwingStartTime(player.ticksExisted);
                anim.setCustomSwingEndTime(player.ticksExisted + message.duration);

                /* Server then auto-informs Clients. */
                PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new SpearSwingAnimMessage(player.getEntityId(), message.duration), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 0.0D));
            }
            return null;
        }
    }
}