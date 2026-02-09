package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends EntityLivingBase {

    public MixinEntityLiving(World worldIn) {
        super(worldIn);
    }

    //fixes bug where multi passenger entities are controlled by both passengers
    //this legitimately took me 3 and a half hours to realise that's why the ai was bugging
    //and another 3 hours to find this method that was causing it
    @WrapOperation(at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;isRiding()Z"), method = "updateEntityActionState")
    public boolean mounts$updateEntityActionState$isRiding(EntityLiving instance, Operation<Boolean> original) {
        Entity entity = getRidingEntity();
        if (entity == null) return original.call(instance);
        List<Entity> passengers = entity.getPassengers();
        if (passengers.isEmpty()) return original.call(instance);
        return passengers.get(0) != this ? false : original.call(instance);
    }

}
