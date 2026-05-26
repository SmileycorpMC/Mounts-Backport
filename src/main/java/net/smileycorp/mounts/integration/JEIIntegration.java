package net.smileycorp.mounts.integration;

import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import net.smileycorp.mounts.api.VanillaSpears;
import net.smileycorp.mounts.config.MountsConfig;

import javax.annotation.Nonnull;

@JEIPlugin
public class JEIIntegration implements IModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        if (!MountsConfig.anvilNetheriteSpearRecipe || Loader.isModLoaded("futuremc") |! OreDictionary.doesOreNameExist("ingotNetherite")
                || VanillaSpears.DIAMOND_SPEAR.get() == null || VanillaSpears.NETHERITE_SPEAR.get() == null) return;
       registry.addRecipes(Lists.newArrayList(registry.getJeiHelpers().getVanillaRecipeFactory().createAnvilRecipe(
               new ItemStack(VanillaSpears.DIAMOND_SPEAR.get()), OreDictionary.getOres("ingotNetherite"),
               Lists.newArrayList(new ItemStack(VanillaSpears.NETHERITE_SPEAR.get())))), VanillaRecipeCategoryUid.ANVIL);
    }


}
