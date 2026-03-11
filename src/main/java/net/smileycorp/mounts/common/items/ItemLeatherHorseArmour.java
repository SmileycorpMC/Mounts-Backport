package net.smileycorp.mounts.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.smileycorp.atlas.api.item.ItemHorseArmourBase;
import net.smileycorp.mounts.common.Constants;

public class ItemLeatherHorseArmour extends ItemHorseArmourBase {

    public ItemLeatherHorseArmour() {
        super(Constants.MODID, "leather", 3, CreativeTabs.MISC);
    }

    public static boolean isDyed(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return false;
        NBTTagCompound display = nbt.getCompoundTag("display");
        return display.hasKey("color", 3);
    }

    public static int getColour(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            NBTTagCompound display = nbt.getCompoundTag("display");
            if (display.hasKey("color", 3)) return display.getInteger("color");
        }
        return 10511680;
    }

    public static void removeColour(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
         if (nbt != null) {
            NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("display");
            if (nbttagcompound1.hasKey("color")) nbttagcompound1.removeTag("color");
        }
    }

    public static void setColor(ItemStack stack, int color) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            NBTTagCompound display = nbt.getCompoundTag("display");
            if (!nbt.hasKey("display", 10)) nbt.setTag("display", display);
            display.setInteger("color", color);
    }

}
