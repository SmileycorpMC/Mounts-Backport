package net.smileycorp.mounts.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAIRunAroundLikeCrazy.class)
public class MixinEntityAIRunAroundLikeCrazy {

    @Shadow @Final private AbstractHorse horseHost;

    @Inject(at = @At("RETURN"), method = "shouldExecute", cancellable = true)
    public void mounts$shouldExecute(CallbackInfoReturnable<Boolean> callback) {
        if (!callback.getReturnValue()) return;
        for (Entity entity : horseHost.getPassengers()) if (entity instanceof EntityPlayer) return;
        callback.setReturnValue(false);
    }

}
