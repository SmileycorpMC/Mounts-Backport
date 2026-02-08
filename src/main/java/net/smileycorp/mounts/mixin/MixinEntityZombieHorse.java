package net.smileycorp.mounts.mixin;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityZombieHorse.class)
public abstract class MixinEntityZombieHorse extends AbstractHorse {

    public MixinEntityZombieHorse(World worldIn) {
        super(worldIn);
    }

    @Inject(at=@At("HEAD"), method = "processInteract", cancellable = true)
    public void mounts$processInteract(CallbackInfoReturnable<Boolean> callback) {
        enablePersistence();
    }

}
