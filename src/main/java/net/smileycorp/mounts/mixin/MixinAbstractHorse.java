package net.smileycorp.mounts.mixin;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.smileycorp.mounts.config.EntityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse extends EntityAnimal {

	@Shadow
	protected ContainerHorseChest horseChest;

	public MixinAbstractHorse(World worldIn) {
		super(worldIn);
	}

	//makes zombie horses burn in sunlight
	//decided to make a config option for skeleton horses too (as that was a feature in 25w41a and reverted in 25w42a)
	@Inject(at=@At("HEAD"), method = "onLivingUpdate()V")
	public void mounts$onLivingUpdate(CallbackInfo callback) {
		if (!((EntityAnimal)this instanceof EntityZombieHorse && EntityConfig.zombieHorsesBurnInSunlight) &!
				((EntityAnimal)this instanceof EntitySkeletonHorse && EntityConfig.skeletonHorsesBurnInSunlight)) return;
		if (world.isRemote || !world.isDaytime()) return;
		ItemStack itemstack = horseChest.getStackInSlot(1);
		if (itemstack.isEmpty()) {
			setFire(8);
			return;
		}
		if (itemstack.isItemStackDamageable()) {
			itemstack.setItemDamage(itemstack.getItemDamage() + rand.nextInt(2));
			if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) horseChest.decrStackSize(1, 1);
		}
	}

	//makes zombie horses burn in sunlight
	//decided to make a config option for skeleton horses too (as that was a feature in 25w41a and reverted in 25w42a)
	@Inject(at= @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"), method = "onInitialSpawn", cancellable = true)
	public void mounts$onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata, CallbackInfoReturnable<IEntityLivingData> callback) {
		if (!((EntityAnimal)this instanceof EntityZombieHorse && EntityConfig.zombieHorsesBurnInSunlight) &!
				((EntityAnimal)this instanceof EntitySkeletonHorse && EntityConfig.skeletonHorsesBurnInSunlight)) return;
		callback.setReturnValue(livingdata);
	}

}
