package net.smileycorp.mounts.mixin;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.smileycorp.mounts.api.ItemSpear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem {

    @Shadow public abstract ItemStack getItem();

    @Inject(at = @At("HEAD"), method = "attackEntityFrom", cancellable = true)
    public void mounts$attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (!source.isFireDamage()) return;
        Item item = getItem().getItem();
        if (item instanceof ItemSpear && ((ItemSpear) item).getDefinition().isFireproof()) callback.setReturnValue(false);
    }

}
