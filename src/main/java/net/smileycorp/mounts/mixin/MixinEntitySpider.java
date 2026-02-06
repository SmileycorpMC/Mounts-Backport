package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.smileycorp.mounts.common.entity.EntityParched;
import net.smileycorp.mounts.config.MountsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(EntitySpider.class)
public abstract class MixinEntitySpider extends EntityMob {

    public MixinEntitySpider(World worldIn) {
        super(worldIn);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), method = "onInitialSpawn")
    public void mounts$onInitialSpawn$spawn(DifficultyInstance difficulty, IEntityLivingData livingdata, CallbackInfoReturnable<IEntityLivingData> cir, @Local EntitySkeleton entityskeleton) {
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(world.getBiome(getPosition()));
        if (!types.contains(BiomeDictionary.Type.SANDY) || types.contains(BiomeDictionary.Type.MESA)
                || types.contains(BiomeDictionary.Type.BEACH)) return;
        if (rand.nextFloat() < MountsConfig.parchedChance) return;
        entityskeleton.setDead();
        EntityParched parched = new EntityParched(world);
        parched.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
        parched.onInitialSpawn(difficulty, null);
        world.spawnEntity(parched);
        parched.startRiding(this);
    }

}
