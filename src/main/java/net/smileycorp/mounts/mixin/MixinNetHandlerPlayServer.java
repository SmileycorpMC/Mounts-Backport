package net.smileycorp.mounts.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {

    @Shadow public EntityPlayerMP player;

    //store the players pos before movement is handled in a capability
    //used for calculating speed in spear damage
    //for some reason vanilla player movement doesn't store these on serverside
    // it also doesn't use the forward move field or motion fields
    //player movement is entirely handled by teleporting them whenever a packet is received
    @Inject(at = @At("HEAD"), method = "processPlayer")
    public void mounts$processPlayer(CPacketPlayer packet, CallbackInfo callback) {
        if (!player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null)) return;
        player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null).setPrevPos(player.posX, player.posY, player.posZ);
    }

}
