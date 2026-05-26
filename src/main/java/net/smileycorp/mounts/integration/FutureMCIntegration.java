package net.smileycorp.mounts.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;
import net.smileycorp.mounts.api.VanillaSpears;
import thedarkcolour.futuremc.recipe.smithing.SmithingRecipe;
import thedarkcolour.futuremc.recipe.smithing.SmithingRecipes;

public class FutureMCIntegration {

    public static void registerNetheriteSpearRecipe() {
        SmithingRecipes.INSTANCE.getRecipes().add(new SmithingRecipe(Ingredient.fromStacks(new ItemStack(VanillaSpears.DIAMOND_SPEAR.get(), 1, 32767)),
                new OreIngredient("ingotNetherite"), new ItemStack(VanillaSpears.NETHERITE_SPEAR.get())));
    }

}
