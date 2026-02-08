package net.smileycorp.mounts.mixin;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityZombieHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAnimal.class)
public abstract class MixinEntityAnimal {

    @Inject(at=@At("HEAD"), method = "canDespawn", cancellable = true)
    public void mounts$canDespawn(CallbackInfoReturnable<Boolean> callback) {
        if ((Object)this instanceof EntityZombieHorse) callback.setReturnValue(true);
    }

}
