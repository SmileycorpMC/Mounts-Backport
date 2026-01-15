package net.smileycorp.mounts.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;

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
}