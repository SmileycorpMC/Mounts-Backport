package net.smileycorp.mounts.mixin;

import net.minecraft.entity.ai.EntityAISkeletonRiders;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.world.DifficultyInstance;
import net.smileycorp.mounts.common.entity.EntitySkeletonRider;
import net.smileycorp.mounts.config.MountsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAISkeletonRiders.class)
public class MixinEntityAISkeletonRiders {

    @Unique
    private int count = 0;

    @Inject(at = @At(value = "HEAD"), method = "createSkeleton", cancellable = true)
    public void hordes$createSkeleton(DifficultyInstance difficulty, AbstractHorse horse, CallbackInfoReturnable<EntitySkeleton> callback) {
        if (!MountsConfig.improvedHorseTraps) return;
        EntitySkeletonRider entity = new EntitySkeletonRider(horse.world);
        entity.onInitialSpawn(difficulty, new EntitySkeletonRider.HorseTrapSpawnData());
        entity.setCirclingAngle(count++ * 90);
        entity.setPosition(horse.posX, horse.posY, horse.posZ);
        entity.hurtResistantTime = 60;
        entity.enablePersistence();
        horse.world.spawnEntity(entity);
        callback.setReturnValue(entity);
    }

}
