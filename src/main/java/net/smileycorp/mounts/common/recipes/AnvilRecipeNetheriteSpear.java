package net.smileycorp.mounts.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.smileycorp.mounts.api.VanillaSpears;

public class AnvilRecipeNetheriteSpear {

    @SubscribeEvent
    public void updateAnvil(AnvilUpdateEvent event) {
        ItemStack spear = event.getLeft();
        if (spear.getItem() != VanillaSpears.DIAMOND_SPEAR.get()) return;
        if (!isNetheriteIngot(event.getRight())) return;
        ItemStack output = new ItemStack(VanillaSpears.NETHERITE_SPEAR.get());
        if (spear.hasTagCompound()) output.setTagCompound(spear.getTagCompound());
        //calculates the rough percentage of durability to keep
        output.setItemDamage((int) (((float)spear.getItemDamage() / (float) spear.getMaxDamage()) * output.getMaxDamage()));
        event.setCost(8);
        event.setMaterialCost(1);
        event.setOutput(output);
    }

    private boolean isNetheriteIngot(ItemStack right) {
        for (ItemStack stack : OreDictionary.getOres("ingotNetherite", false))
            if (OreDictionary.itemMatches(right, stack, false)) return true;
        return false;
    }

}
