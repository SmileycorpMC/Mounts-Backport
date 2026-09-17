package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySkeletonHorse.class)
public abstract class MixinEntitySkeletonHorse extends AbstractHorse {

    public MixinEntitySkeletonHorse(World worldIn) {
        super(worldIn);
    }

    //stop zombie horses despawning and contributing to mob cap if a player tries to ride them
    @Inject(at=@At("HEAD"), method = "processInteract")
    public void mounts$processInteract$HEAD(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> callback) {
        enablePersistence();
    }

    @Inject(at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", shift = At.Shift.AFTER), method = "processInteract", cancellable = true)
    public void mounts$processInteract$isEmpty(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> callback, @Local ItemStack itemstack) {
        if (!itemstack.isEmpty() && handleEating(player, itemstack)) {
            if (!player.capabilities.isCreativeMode) itemstack.shrink(1);
            callback.setReturnValue(true);
        }
        if (isTame()) return;
        if (itemstack.getItem() == Items.SPAWN_EGG) {
            callback.setReturnValue(super.processInteract(player, hand));
            return;
        }
        if (itemstack.isEmpty()) mountTo(player);
        else makeMad();
        callback.setReturnValue(true);
    }

}
