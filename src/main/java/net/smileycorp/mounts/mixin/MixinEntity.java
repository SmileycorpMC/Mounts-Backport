package net.smileycorp.mounts.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract boolean isPassenger(Entity entityIn);

    @Shadow public double posX;

    @Shadow public double posY;

    @Shadow public double posZ;

    @Shadow public float rotationYaw;

    @Shadow public abstract double getMountedYOffset();

    @Shadow public abstract float getRotationYawHead();

    @Shadow public abstract Vec3d getLookVec();

    @Inject(at = @At("HEAD"), method = "updatePassenger", cancellable = true)
    public void mounts$updatePassenger(Entity passenger, CallbackInfo callback) {
        if (!isPassenger(passenger) |! (((Object)this)instanceof EntityZombie) |! (passenger instanceof EntityZombie)) return;
        Vec3d offset = new Vec3d(0, 0, -0.4f).rotateYaw(-this.rotationYaw * 0.017453292f);
        passenger.setPosition(posX + offset.x, posY + getMountedYOffset() - 0.35, posZ + offset.z);
        callback.cancel();
    }

}
