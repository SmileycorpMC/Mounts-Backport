package net.smileycorp.mounts.common.items;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.item.ItemHorseArmourBase;
import net.smileycorp.mounts.common.Constants;

public class ItemLeatherHorseArmour extends ItemHorseArmourBase {

    public ItemLeatherHorseArmour() {
        super(Constants.MODID, "leather", 3, CreativeTabs.MISC);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != this |! isDyed(stack)) return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockCauldron)) return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        int level = state.getValue(BlockCauldron.LEVEL);
        if (level == 0) return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (!world.isRemote) {
            removeColour(stack);
            Blocks.CAULDRON.setWaterLevel(world, pos, state, level - 1);
            player.addStat(StatList.ARMOR_CLEANED);
            world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public String getHorseArmorTexture(EntityLiving wearer, ItemStack stack) {
        return null;
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
