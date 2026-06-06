package net.smileycorp.mounts.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearAnimation;

public class SpearRecoilAnimMessage implements IMessage
{
    private int entityId;

    public SpearRecoilAnimMessage() {}

    public SpearRecoilAnimMessage(int entityId) { this.entityId = entityId; }

    @Override
    public void fromBytes(ByteBuf buf) { entityId = buf.readInt(); }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeInt(entityId); }

    public static class Handler implements IMessageHandler<SpearRecoilAnimMessage, IMessage>
    {
        @Override
        public IMessage onMessage(SpearRecoilAnimMessage message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                Entity player = Minecraft.getMinecraft().world.getEntityByID(message.entityId);

                if(player.hasCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null))
                {
                    CapabilitySpearAnimation.ICapabilityAnimations anim = player.getCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null);
                    anim.setSpearRecoilStartTime(player.ticksExisted);
                }
            });
            return null;
        }
    }
}