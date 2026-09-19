package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.smileycorp.mounts.common.entity.ai.EntityAIFindMount;
import net.smileycorp.mounts.config.EntityConfig;
import net.smileycorp.mounts.config.data.mobs.MobDataLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldEntitySpawner.class)
public class MixinWorldEntitySpawner {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER), method = "findChunksForSpawning")
    private void mounts$findChunksForSpawning$spawnEntity(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> callback, @Local EntityLiving entityliving) {
        if (!entityliving.isRiding() &! entityliving.isBeingRidden() && EntityConfig.isJockeyRider(entityliving) && entityliving.getRNG().nextFloat() <= EntityConfig.jockeyChance) {
            entityliving.tasks.addTask(1, new EntityAIFindMount(entityliving));
            return;
        }
        if (entityliving.isRiding()) return;
        MobDataLoader.INSTANCE.applyData(entityliving);
    }

}
