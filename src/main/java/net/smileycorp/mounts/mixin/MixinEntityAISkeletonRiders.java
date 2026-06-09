package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ai.EntityAISkeletonRiders;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.config.SpearRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAISkeletonRiders.class)
public class MixinEntityAISkeletonRiders {

    @Unique
    private int mounts$spearSkeletons = 2;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntitySkeleton;enablePersistence()V"), method = "createSkeleton")
    public void mounts$createSkeleton$enablePersistence(DifficultyInstance difficultyInstance, AbstractHorse horse, CallbackInfoReturnable<EntitySkeleton> callback, @Local EntitySkeleton entityskeleton) {
        if (mounts$spearSkeletons <= 0) return;
        ItemSpear spear = SpearRegistry.getSpear("iron");
        if (spear == null) {
            mounts$spearSkeletons = 0;
            return;
        }
        entityskeleton.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(spear));
        mounts$spearSkeletons--;
    }

}
