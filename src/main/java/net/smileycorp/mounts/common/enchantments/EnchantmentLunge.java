package net.smileycorp.mounts.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.smileycorp.mounts.common.Constants;

public class EnchantmentLunge extends Enchantment {

    protected EnchantmentLunge() {
        super(Rarity.UNCOMMON, MountsEnchantments.SPEAR, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        setRegistryName(Constants.loc("lunge"));
        setName(Constants.name("lunge"));
    }

    @Override
    public int getMinEnchantability(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return 25 + (level - 1) * 8;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack);
    }
}
