package net.smileycorp.mounts.integration;


import git.jbredwards.smithing_table.api.SmithingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreIngredient;
import net.smileycorp.mounts.api.VanillaSpears;
import net.smileycorp.mounts.common.Constants;

public class SmithingTableIntegration {

    public static void registerNetheriteSpearRecipe() {
        SmithingRecipe.Impl recipe = new SmithingRecipe.Impl(ItemStack.EMPTY, VanillaSpears.DIAMOND_SPEAR.get(), new OreIngredient("ingotNetherite"), new ItemStack(VanillaSpears.NETHERITE_SPEAR.get()));
        recipe.setRegistryName(Constants.loc("netherite_spear"));
        SmithingRecipe.REGISTRY.register(recipe);
    }

}
