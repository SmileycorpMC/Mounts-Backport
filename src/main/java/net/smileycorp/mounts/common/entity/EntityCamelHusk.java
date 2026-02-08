package net.smileycorp.mounts.common.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.mounts.common.MountsLootTables;
import net.smileycorp.mounts.common.MountsSoundEvents;

import javax.annotation.Nullable;

public class EntityCamelHusk extends EntityCamel
{
    public EntityCamelHusk(World worldIn)
    {
        super(worldIn);
        this.setSize(1.7F, 2.375F);
    }

    @Nullable
    protected ResourceLocation getLootTable() { return MountsLootTables.CAMEL_HUSK_DROPS; }

    protected SoundEvent getAmbientSound() { return MountsSoundEvents.CAMEL_HUSK_AMBIENT; }
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return MountsSoundEvents.CAMEL_HUSK_HURT; }
    protected SoundEvent getDeathSound() { return MountsSoundEvents.CAMEL_HUSK_DEATH; }
    public SoundEvent getDashSound() { return MountsSoundEvents.CAMEL_HUSK_DASH; }
    public SoundEvent getDashReadySound() { return MountsSoundEvents.CAMEL_HUSK_DASH_READY; }
    public SoundEvent getStandSound() { return MountsSoundEvents.CAMEL_HUSK_STAND; }
    public SoundEvent getSitSound() { return MountsSoundEvents.CAMEL_HUSK_SIT; }
    public SoundEvent getEatSound() { return MountsSoundEvents.CAMEL_HUSK_EAT; }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        SoundEvent sound = MountsSoundEvents.CAMEL_HUSK_STEP;
        if (blockIn instanceof BlockSand) sound = MountsSoundEvents.CAMEL_HUSK_STEP_SAND;

        this.playSound(sound, 0.5F, 1.0F);
    }

    public EnumCreatureAttribute getCreatureAttribute() { return EnumCreatureAttribute.UNDEAD; }

    /* Camel Husks eat feet???? */
    public boolean isBreedingItem(ItemStack stack) { return stack.getItem() == Items.RABBIT_FOOT; }
    /* Camel Husks are probably aroace, but also are freaks for eating feet. */
    public boolean canMateWith(EntityAnimal otherAnimal) { return false; }

}