package net.smileycorp.mounts.mixin;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.smileycorp.mounts.common.entity.IWearsHorseArmor;
import net.smileycorp.mounts.config.EntityConfig;
import net.smileycorp.mounts.integration.WornHorseshoesIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse extends EntityAnimal implements IWearsHorseArmor {

	@Shadow protected abstract void eatingHorse();

	@Shadow public abstract boolean isTame();

	@Shadow public abstract int getTemper();

	@Shadow public abstract int getMaxTemper();

	@Shadow public abstract int increaseTemper(int p_110198_1_);

	public MixinAbstractHorse(World worldIn) {
		super(worldIn);
	}

	//makes zombie horses burn in sunlight
	//decided to make a config option for skeleton horses too (as that was a feature in 25w41a and reverted in 25w42a)
	@Inject(at=@At("HEAD"), method = "onLivingUpdate()V")
	public void mounts$onLivingUpdate(CallbackInfo callback) {
		if (!((EntityAnimal)this instanceof EntityZombieHorse && EntityConfig.zombieHorsesBurnInSunlight) &!
				((EntityAnimal)this instanceof EntitySkeletonHorse && EntityConfig.skeletonHorsesBurnInSunlight)) return;
		if (world.isRemote) return;
		float f = getBrightness();
		if (f <= 0.5f || rand.nextFloat() * 30f >= (f - 0.4f) * 2f |! world.canSeeSky(new BlockPos(posX, posY + getEyeHeight(), posZ))) return;
		ItemStack stack = getHorseArmour();
		if (stack.isEmpty()) {
			setFire(8);
			return;
		}
		if (stack.isItemStackDamageable()) {
			stack.setItemDamage(stack.getItemDamage() + rand.nextInt(2));
			if (stack.getItemDamage() >= stack.getMaxDamage()) stack.shrink(1);
		}
	}

	//prevent zombie and skeleton horses spawning as babies
	@Inject(at= @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"), method = "onInitialSpawn", cancellable = true)
	public void mounts$onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata, CallbackInfoReturnable<IEntityLivingData> callback) {
		if (!((EntityAnimal)this instanceof EntityZombieHorse) &!((EntityAnimal)this instanceof EntitySkeletonHorse)) return;
		callback.setReturnValue(livingdata);
	}

	@Override
	public ItemStack getHorseArmour() {
		return Loader.isModLoaded("wornhorseshoes") ? WornHorseshoesIntegration.getHorseArmor((AbstractHorse)(EntityAnimal)this) : ItemStack.EMPTY;
	}

	//zombie horse feeding
	@Inject(at= @At("HEAD"), method = "handleEating", cancellable = true)
	public void mounts$handleEating(EntityPlayer player, ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		if (!((EntityAnimal)this instanceof EntityZombieHorse)) return;
		if (!EntityConfig.isZombieHorseFood(stack)) {
			callback.setReturnValue(false);
			return;
		}
		if (getHealth() < getMaxHealth()) heal(3);
		else if (!isTame() && getTemper() < getMaxTemper()) {
			if (!world.isRemote) increaseTemper(3);
			System.out.println("weeweww");
		}
		else {
			System.out.println("wawoo");
			callback.setReturnValue(false);
			return;
		}
		System.out.println("wazanga");
		eatingHorse();
		callback.setReturnValue(true);
	}

}
