package net.smileycorp.mounts.mixin;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAIAttackMelee.class)
public class MixinEntityAIAttackMelee {

    @Shadow protected EntityCreature attacker;

    @Shadow protected int attackTick;

    @Inject(at = @At("HEAD"), method = "getAttackReachSqr", cancellable = true)
    public void hordes$getAttackReachSqr(EntityLivingBase attackTarget, CallbackInfoReturnable<Double> callback) {
        ItemStack stack = attacker.getHeldItemMainhand();
        Item item = stack.getItem();
        if (!(item instanceof ItemSpear)) return;
        callback.setReturnValue((double)((ItemSpear) item).getDefinition().getMaxRange());
    }

    @Inject(at = @At("HEAD"), method = "checkAndPerformAttack", cancellable = true)
    protected void checkAndPerformAttack(EntityLivingBase attackTarget, double distance, CallbackInfo callback) {
        ItemStack stack = attacker.getHeldItemMainhand();
        Item item = stack.getItem();
        if (!(item instanceof ItemSpear)) return;
        callback.cancel();
        SpearDefinition definition = ((ItemSpear) item).getDefinition();
        if (distance > definition.getMaxRange() || attackTick > 0) return;
        ItemSpear.performJabAttack(attacker, stack);
        attackTick = 20;
    }

}
