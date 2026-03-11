package net.smileycorp.mounts.common.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.items.ItemLeatherHorseArmour;
import net.smileycorp.mounts.common.items.MountsItems;

import java.util.Optional;

public class RecipeLeatherHorseArmourDyeing extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public RecipeLeatherHorseArmourDyeing() {
        setRegistryName(Constants.loc("leather_horse_armour_dyeing"));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack armour = ItemStack.EMPTY;
        boolean dye = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == MountsItems.LEATHER_HORSE_ARMOUR) {
                if (!armour.isEmpty()) return false;
                armour = stack;
                continue;
            }
            if (!DyeUtils.isDye(stack)) return false;
            dye = true;
        }
        return !armour.isEmpty() && dye;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack armour = ItemStack.EMPTY;
        int r = 0, g = 0, b = 0;
        int max = 0;
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == MountsItems.LEATHER_HORSE_ARMOUR) {
                if (!armour.isEmpty()) return ItemStack.EMPTY;
                armour = stack.copy();
                if (ItemLeatherHorseArmour.isDyed(stack)) {
                    int colour = ItemLeatherHorseArmour.getColour(stack);
                    int r1 = colour >> 16 & 255;
                    int g1 = colour >> 8 & 255;
                    int b1 = colour & 255;
                    max += Math.max(r1, Math.max(g1, b1));
                    r += r1;
                    g += g1;
                    b += b1;
                    count++;
                }
                continue;
            }
            Optional<EnumDyeColor> optional = DyeUtils.colorFromStack(stack);
            if (!optional.isPresent()) return ItemStack.EMPTY;
            float[] colour = optional.get().getColorComponentValues();
            int r1 = (int) (colour[0] * 255f);
            int g1 = (int) (colour[1] * 255f);
            int b1 = (int) (colour[2] * 255f);
            max += Math.max(r1, Math.max(g1, b1));
            r += r1;
            g += g1;
            b += b1;
            count++;
        }
        if (!armour.isEmpty()) {
            r /= count;
            g /= count;
            b /= count;
            float factor = (float) max / (float) count;
            max = Math.max(r, Math.max(g, b));
            r = (int) ((float)r * factor / max);
            g = (int) ((float)g * factor / max);
            b = (int) ((float)b * factor / max);
            ItemLeatherHorseArmour.setColor(armour, (r << 16) + (g << 8) + b);
        }
        return armour;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(MountsItems.LEATHER_HORSE_ARMOUR);
    }

}
