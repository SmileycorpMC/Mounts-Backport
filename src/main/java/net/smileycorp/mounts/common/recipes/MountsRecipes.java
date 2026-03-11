package net.smileycorp.mounts.common.recipes;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.config.SpearRegistry;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class MountsRecipes {

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        //spear recipes
        for (ItemSpear spear : SpearRegistry.getSpears()) {
            SpearDefinition def = spear.getDefinition();
            if (!def.isCraftable()) continue;
            ShapedOreRecipe recipe = new ShapedOreRecipe(Constants.loc("spear"), new ItemStack(spear),
                    CraftingHelper.parseShaped( "  M", " S ", "S  ", 'M', def.getRepairMaterial(), 'S', "stickWood"));
            recipe.setRegistryName(spear.getRegistryName());
            registry.register(recipe);
        }

        //horse armour dying
        registry.register(new RecipeLeatherHorseArmourDyeing());

        //trades
        ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:butcher")).getCareer(1)
                .addTrade(4, new TradeLeatherHorseArmour(new ItemStack(Items.EMERALD), new EntityVillager.PriceInfo(6, 6)));
    }

}
