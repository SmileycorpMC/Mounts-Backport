package net.smileycorp.mounts.common.recipes;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.smileycorp.mounts.common.items.ItemLeatherHorseArmour;
import net.smileycorp.mounts.common.items.MountsItems;

import java.util.Random;

public class TradeLeatherHorseArmour implements EntityVillager.ITradeList {

    private final ItemStack currency;
    private final EntityVillager.PriceInfo price;

    public TradeLeatherHorseArmour(ItemStack currency, EntityVillager.PriceInfo price) {
        this.currency = currency;
        this.price = price;
    }

    @Override
    public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random rand) {
        ItemStack buy = currency.copy();
        buy.setCount(price.getPrice(rand));
        ItemStack sell = new ItemStack(MountsItems.LEATHER_HORSE_ARMOUR);
        float[] colour1 = EnumDyeColor.values()[rand.nextInt(EnumDyeColor.values().length)].getColorComponentValues();
        float[] colour2 = EnumDyeColor.values()[rand.nextInt(EnumDyeColor.values().length)].getColorComponentValues();
        int r = (int) ((colour1[0] + colour2[0]) * 255f) / 2;
        int g = (int) ((colour1[1] + colour2[1]) * 255f) / 2;
        int b = (int) ((colour1[2] + colour2[2]) * 255f) / 2;
        ItemLeatherHorseArmour.setColor(sell, (r << 16) + (g << 8) + b);
        recipeList.add(new MerchantRecipe(buy, sell));
    }

}
