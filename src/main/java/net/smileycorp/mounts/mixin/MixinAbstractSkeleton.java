package net.smileycorp.mounts.mixin;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import net.smileycorp.mounts.common.entity.EntityParched;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used for the Parched to skip any of AbstractSkeleton's Daylight logic (Helmet Damage + Burning)
 */
@Mixin(AbstractSkeleton.class)
public abstract class MixinAbstractSkeleton extends EntityMob implements IRangedAttackMob
{
    public MixinAbstractSkeleton(World worldIn) { super(worldIn); }

    @Inject(at = @At("HEAD"), method = "onLivingUpdate", cancellable = true)
    public void mounts$updatePassenger(CallbackInfo callback)
    {
        if ((((Object)this)instanceof EntityParched))
        {
            super.onLivingUpdate();
            callback.cancel();
        }
    }
}