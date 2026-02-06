package net.smileycorp.mounts.common.entity;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.smileycorp.mounts.common.MountsLootTables;
import net.smileycorp.mounts.common.MountsSoundEvents;
import net.smileycorp.mounts.config.EntityConfig;

import javax.annotation.Nullable;
import java.util.Iterator;

public class EntityParched extends AbstractSkeleton
{
    private final EntityAIAttackRangedBow<AbstractSkeleton> aiArrowAttack = new EntityAIAttackRangedBow(this, 1.0, 20, 15.0F);
    private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2, false)
    {
        public void resetTask()
        {
            super.resetTask();
            EntityParched.this.setSwingingArms(false);
        }

        public void startExecuting()
        {
            super.startExecuting();
            EntityParched.this.setSwingingArms(true);
        }
    };

    public EntityParched(World worldIn)
    {
        super(worldIn);
    }


    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        EntityConfig.parched.applyAttributes(this);
    }

    /* A copy of `initEntityAI` with fleeing from the sun removed. */
    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
    }

    /** Overrides Combat Task, so we may customize the shooting speed. */
    @Override
    public void setCombatTask()
    {
        super.setCombatTask();

        /* After the Skeleton's Combat AI Tasks are set, we rip out. */
        if (this.world != null && !this.world.isRemote)
        {
            /* Currently uses an iterator to find and remove the `EntityAIAttackRangedBow`, as a simple `removeTask` wasn't working. */
            Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.tasks.taskEntries.iterator();
            while (iterator.hasNext())
            {
                EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
                EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;

                if (entityaibase instanceof EntityAIAttackRangedBow)
                { iterator.remove(); }
            }

            ItemStack itemstack = this.getHeldItemMainhand();
            if (itemstack.getItem() instanceof ItemBow)
            {
                int i = 20;
                switch (world.getDifficulty()) {
                    case EASY:
                        i += 3.5;
                        break;
                    default:
                        i += 3.5;
                        break;
                    case HARD:
                        i += 2.5F;
                        break;
                }

                this.aiArrowAttack.setAttackCooldown(i);
                this.tasks.addTask(4, this.aiArrowAttack);
            }
        }
    }

    @Nullable
    protected ResourceLocation getLootTable() { return MountsLootTables.PARCHED_DROPS; }

    protected SoundEvent getAmbientSound() { return MountsSoundEvents.PARCHED_AMBIENT; }
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return MountsSoundEvents.PARCHED_HURT; }
    protected SoundEvent getDeathSound() { return MountsSoundEvents.PARCHED_DEATH; }
    protected SoundEvent getStepSound() { return MountsSoundEvents.PARCHED_STEP; }

    protected EntityArrow getArrow(float p_190726_1_)
    {
        EntityArrow entityarrow = super.getArrow(p_190726_1_);
        if (entityarrow instanceof EntityTippedArrow)
        { ((EntityTippedArrow)entityarrow).addEffect(new PotionEffect(MobEffects.WEAKNESS, 30 * 20)); }
        return entityarrow;
    }
}