package net.smileycorp.mounts.common.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.config.EntityConfig;
import net.smileycorp.mounts.config.SpearRegistry;

public class EntitySkeletonHorseman extends EntitySkeleton {

    public static final DataParameter<ItemStack> BACK_ITEM = EntityDataManager.createKey(EntitySkeletonHorseman.class, DataSerializers.ITEM_STACK);

    public EntitySkeletonHorseman(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(BACK_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        EntityConfig.skeletonHorseman.applyAttributes(this);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        ItemSpear spear = SpearRegistry.getSpear("iron");
        setBackItem(new ItemStack(spear == null ? Items.IRON_SWORD : spear));
        setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    }

    @Override
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
        setItemStackToSlot(EntityEquipmentSlot.MAINHAND, EnchantmentHelper.addRandomEnchantment(rand, getHeldItemMainhand(), (int)(5f + difficulty.getClampedAdditionalDifficulty() * (float)rand.nextInt(18)), false));
        setBackItem(EnchantmentHelper.addRandomEnchantment(rand, getBackItem(), (int)(5f + difficulty.getClampedAdditionalDifficulty() * (float)rand.nextInt(18)), false));
        setItemStackToSlot(EntityEquipmentSlot.HEAD, EnchantmentHelper.addRandomEnchantment(rand, getItemStackFromSlot(EntityEquipmentSlot.HEAD), (int)(5f + difficulty.getClampedAdditionalDifficulty() * (float)rand.nextInt(18)), false));
    }

    public ItemStack getBackItem() {
        return dataManager.get(BACK_ITEM);
    }

    public void setBackItem(ItemStack stack) {
        dataManager.set(BACK_ITEM, stack);
    }

    public void swapWeapon() {
        ItemStack stack = getHeldItemMainhand();
        setHeldItem(EnumHand.MAIN_HAND, getBackItem());
        setBackItem(stack);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        ItemStack stack = getBackItem();
        if (!stack.isEmpty()) nbt.setTag("BackItem", stack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("BackItem")) setBackItem(new ItemStack(nbt.getCompoundTag("BackItem")));
    }

}
