package net.smileycorp.mounts.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.common.network.PacketHandler;
import net.smileycorp.mounts.common.network.SpearAttackMessage;
import net.smileycorp.mounts.config.SpearRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow @Final private List<IResourcePack> defaultResourcePacks;

    @Shadow public EntityPlayerSP player;

    @Shadow public RayTraceResult objectMouseOver;

    @Inject(at = @At("HEAD"), method = "init")
    public void mounts$getResourcePackFiles(CallbackInfo callback) {
        try {
            SpearRegistry.generateData();
            defaultResourcePacks.add(new FolderResourcePack(SpearRegistry.CONFIG_FOLDER));
        } catch (Exception e) {
            MountsLogger.logError("Failed loading config resources", e);
        }
    }

    @Inject(at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/entity/EntityPlayerSP;isRowingBoat()Z"), method = "clickMouse", cancellable = true)
    public void mounts$clickMouse(CallbackInfo callback) {
        ItemStack stack = player.getHeldItemMainhand();
        if (!(stack.getItem() instanceof ItemSpear)) return;
        callback.cancel();
        if (player.getCooledAttackStrength(0) < 1) return;
        PacketHandler.NETWORK_INSTANCE.sendToServer(new SpearAttackMessage());
        player.resetCooldown();
    }

    @Inject(at = @At(value = "HEAD"), method = "sendClickBlockToController", cancellable = true)
    public void mounts$sendClickBlockToController(boolean leftClick, CallbackInfo callback) {
        if (!leftClick) return;
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemSpear) callback.cancel();
    }


}
