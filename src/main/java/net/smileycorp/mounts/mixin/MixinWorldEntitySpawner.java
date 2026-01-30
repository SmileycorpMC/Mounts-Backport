package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.WorldEntitySpawner;
import net.smileycorp.mounts.common.entities.ai.EntityAIFindMount;
import net.smileycorp.mounts.config.MountsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldEntitySpawner.class)
public class MixinWorldEntitySpawner {

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;onInitialSpawn(Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/entity/IEntityLivingData;)Lnet/minecraft/entity/IEntityLivingData;"), method = "findChunksForSpawning")
    public IEntityLivingData mounts$findChunksForSpawning(EntityLiving instance, DifficultyInstance difficulty, IEntityLivingData livingdata, Operation<IEntityLivingData> original) {
        livingdata = original.call(instance, difficulty, livingdata);
        if (MountsConfig.babyZombieJockeyChance <= 0) return livingdata;
        if (!(instance instanceof EntityZombie) |! instance.isChild()) return livingdata;
        if (instance.getRNG().nextFloat() > MountsConfig.babyZombieJockeyChance) return livingdata;
        instance.tasks.addTask(1, new EntityAIFindMount(instance));
        return livingdata;
    }

}
