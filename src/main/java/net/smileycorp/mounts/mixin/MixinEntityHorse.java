package net.smileycorp.mounts.mixin;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import net.smileycorp.mounts.common.entity.IWearsHorseArmor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityHorse.class)
public abstract class MixinEntityHorse extends AbstractHorse implements IWearsHorseArmor {

	@Shadow(remap = false) @Final private static DataParameter<ItemStack> HORSE_ARMOR_STACK;

	public MixinEntityHorse(World worldIn) {
		super(worldIn);
	}

	@Override
	public ItemStack getHorseArmour() {
		return dataManager.get(HORSE_ARMOR_STACK);
	}

}
