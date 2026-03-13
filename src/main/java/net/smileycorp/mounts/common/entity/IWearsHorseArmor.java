package net.smileycorp.mounts.common.entity;

import net.minecraft.item.ItemStack;

public interface IWearsHorseArmor {

    default ItemStack getHorseArmour() {
        return ItemStack.EMPTY;
    }

}
