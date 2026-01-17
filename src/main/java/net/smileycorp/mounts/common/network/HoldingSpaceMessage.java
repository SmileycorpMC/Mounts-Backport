package net.smileycorp.mounts.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;

public class HoldingSpaceMessage implements IMessage
{
    private int entityId;
    private boolean isJumping;

    public HoldingSpaceMessage() {}

    public HoldingSpaceMessage(int entityIdIn, boolean isJumping)
    {
        this.entityId = entityIdIn;
        this.isJumping = isJumping;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityId = buf.readInt();
        isJumping = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityId);
        buf.writeBoolean(isJumping);
    }

    public IMessage process(MessageContext ctx)
    {
        if (ctx.side == Side.SERVER) FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null))
            {
                player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null).setIsSpaceHeld(isJumping);
            }
        });
        return null;
    }
}