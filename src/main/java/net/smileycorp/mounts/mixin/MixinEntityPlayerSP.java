package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.entity.EntityPlayerSP;
import net.smileycorp.mounts.api.ItemSpear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {

    //stink ass vanilla slowing me down for holding right click
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"), method = "onLivingUpdate")
    public boolean mounts$init(EntityPlayerSP instance, Operation<Boolean> original) {
        return instance.getActiveItemStack().getItem() instanceof ItemSpear ? false : original.call(instance);
    }

}
