package net.smileycorp.mounts.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract boolean isPassenger(Entity entityIn);

    @Shadow public double posX;

    @Shadow public double posY;

    @Shadow public double posZ;

    @Shadow public float rotationYaw;

    @Shadow public abstract double getMountedYOffset();

    @Shadow public abstract List<Entity> getPassengers();

    @Shadow private Entity ridingEntity;

    @Inject(at = @At("HEAD"), method = "updatePassenger", cancellable = true)
    public void mounts$updatePassenger(Entity passenger, CallbackInfo callback) {
        if (!isPassenger(passenger) |! (((Object)this)instanceof EntityZombie) |! (passenger instanceof EntityZombie)) return;
        Vec3d offset = new Vec3d(0, 0, -0.4f).rotateYaw(-rotationYaw * 0.017453292f);
        passenger.setPosition(posX + offset.x, posY + getMountedYOffset() - 0.35, posZ + offset.z);
        callback.cancel();
    }

    @Inject(at = @At("HEAD"), method = "getControllingPassenger", cancellable = true)
    public void mounts$getControllingPassenger(CallbackInfoReturnable<Entity> callback) {
        if (!(((Object)this) instanceof EntityAnimal)) return;
        if (!getPassengers().isEmpty()) callback.setReturnValue(getPassengers().get(0));
    }

    @Inject(at = @At("HEAD"), method = "isCreatureType", cancellable = true, remap = false)
    public void mounts$isCreatureType(EnumCreatureType type, boolean forSpawnCount, CallbackInfoReturnable<Boolean> callback) {
        if (((Object) this) instanceof EntityZombieHorse) callback.setReturnValue(type ==
                (forSpawnCount ? EnumCreatureType.MONSTER : EnumCreatureType.CREATURE));
    }

    //uhhhhhh like the other one but other projectiles than arrows use this, I think Idk, is this even needed?
    @Inject(at = @At("HEAD"), method = "isEntityEqual", cancellable = true)
    public void mounts$isEntityEqual(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (ridingEntity == entity.getRidingEntity()) callback.setReturnValue(true);
    }

}
