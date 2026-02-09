package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityArrow.class)
public class MixinEntityArrow {

    @Shadow public Entity shootingEntity;

    //prevent projectiles from entities from hitting other passengers
    //it might be funny at first, but probably not good game design for every husk camel jockey to immediately implode into monster infighting (doom reference?!?!?!??!)
    //the projectile impact event, still makes the projectile impact when cancelled
    //all it does is cancel the damage and deletion of the arrow...    and kills it's entire momentum
    //this actually sucks
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;calculateIntercept(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"), method = "findEntityOnPath")
    public RayTraceResult mounts$findEntityOnPath(AxisAlignedBB instance, Vec3d vecA, Vec3d vecB, Operation<RayTraceResult> original, @Local(ordinal = 1) Entity entity1) {
        return shootingEntity != null && entity1.getRidingEntity() == shootingEntity.getRidingEntity() ? null : original.call(instance, vecA, vecB);
    }

}
