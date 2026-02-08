package net.smileycorp.mounts.common.items;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.Jockeys;

public class ItemJockeySpawner extends Item {

    public ItemJockeySpawner() {
        setRegistryName(Constants.loc("jockey_spawner"));
        setUnlocalizedName("jockey_spawner");
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, (source, stack) -> {
            EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
            double x = source.getX() + enumfacing.getFrontOffsetX();
            double y = source.getBlockPos().getY() + enumfacing.getFrontOffsetY() + 0.2F;
            double z = source.getZ() + enumfacing.getFrontOffsetZ();
            spawnEntity(source.getWorld(), stack.getMetadata(), x, y, z);
            stack.shrink(1);
            return stack;
        });
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Jockeys.Type type = Jockeys.Type.get(stack.getMetadata());
        StringBuilder builder = new StringBuilder(I18n.translateToLocal("item.monsterPlacer.name").trim());
        if (type != null) builder.append(" " + I18n.translateToLocal(type.getUnlocalizedName()).trim());
        return builder.toString();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) return;
        for (int i = 0; i < Jockeys.Type.values().length; i++) items.add(new ItemStack(this, 1, i));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.PASS, stack);
        RayTraceResult ray = rayTrace(world, player, true);
        if (ray == null) return new ActionResult<>(EnumActionResult.PASS, stack);
        if (ray.typeOfHit != RayTraceResult.Type.BLOCK) return new ActionResult<>(EnumActionResult.PASS, stack);
        BlockPos pos = ray.getBlockPos().offset(ray.sideHit);
        if (!world.isBlockModifiable(player, pos) && player.canPlayerEdit(pos, ray.sideHit, stack))
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (spawnEntity(world, stack.getMetadata(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) == null)
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (!player.capabilities.isCreativeMode) stack.shrink(1);
        player.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public EntityLiving spawnEntity(World world, int metadata, double x, double y, double z) {
        Jockeys.Type type = Jockeys.Type.get(metadata);
        return type == null ? null : Jockeys.spawn(world, type, x, y, z);
    }

    public static int getColours(ItemStack stack, int tintIndex) {
        Jockeys.Type type = Jockeys.Type.get(stack.getMetadata());
        if (type == null) return -1;
        EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(type.getEntity());
        return eggInfo == null ? -1 : tintIndex == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor;
    }

}
