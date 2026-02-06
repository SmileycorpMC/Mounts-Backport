package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;
import net.smileycorp.mounts.common.MountsCommonEvents;
import net.smileycorp.mounts.config.MountsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(EntitySpider.class)
public abstract class MixinEntitySpider extends EntityMob {

    public MixinEntitySpider(World worldIn) {
        super(worldIn);
    }

    //disable vanilla spider jockey spawning and replace them with our own
    @WrapOperation(at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"), method = "onInitialSpawn")
    public int mounts$onInitialSpawn$nextInt(Random instance, int i, Operation<Integer> original) {
        if (world.rand.nextFloat() <= MountsConfig.spiderJockeyChance) MountsCommonEvents.addRider(this);
        return 1;
    }

}
