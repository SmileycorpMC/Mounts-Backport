package net.smileycorp.mounts.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.entity.EntityCamel;
import net.smileycorp.mounts.common.network.HoldingSpaceMessage;
import net.smileycorp.mounts.common.network.PacketHandler;

@Mod.EventBusSubscriber
public class MountsCommonEvents
{
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(CapabilitySpearMovement.ID, new CapabilitySpearMovement.Provider(new CapabilitySpearMovement.MountsPlayerInfoMethods(), CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null));
        }
    }

    /** This informs the server that the Player has pressed Space. */
    @SubscribeEvent
    public static void onUpdateJump(InputUpdateEvent event)
    {
        boolean isJumping = event.getMovementInput().jump;

        EntityPlayer player = event.getEntityPlayer();

        if (player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null))
        {
            CapabilitySpearMovement.ICapabilityMountsPlayerInfo capCharge = player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null);

            if (isJumping != capCharge.getIsSpaceHeld())
            {
                PacketHandler.NETWORK_INSTANCE.sendToServer(new HoldingSpaceMessage(event.getEntityPlayer().getEntityId(), isJumping));
                capCharge.setIsSpaceHeld(isJumping);
            }
        }
    }

    /** Bonus security check */
    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        if (!player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null)) return;
        CapabilitySpearMovement.ICapabilityMountsPlayerInfo capCharge = player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null);
        //reset player previous position so speed doesn't get stored
        capCharge.setPrevPos(player.posX, player.posY, player.posZ);

        //camel code
        if (player.getRidingEntity() instanceof EntityCamel) {
            EntityCamel camel = (EntityCamel)player.getRidingEntity();
            if (camel.isSitting() || camel.dashCooldown > 0) return;
            if (capCharge.getIsSpaceHeld()) capCharge.setSpaceHeldTime(Math.min(1.0F, capCharge.getSpaceHeldTime() + 0.1F));
        }
    }

}