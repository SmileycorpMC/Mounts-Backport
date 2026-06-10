package net.smileycorp.mounts.common.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.config.EntityConfig;
import net.smileycorp.mounts.config.SpearRegistry;

import javax.annotation.Nullable;

public class EntitySkeletonRider extends EntitySkeleton {

    public static final DataParameter<ItemStack> BACK_ITEM = EntityDataManager.createKey(EntitySkeletonRider.class, DataSerializers.ITEM_STACK);
    public boolean sheduledMainhandSwap = false;
    private int circlingAngle = 0;

    public EntitySkeletonRider(World world) {
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

    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(2, new EntityAIRiderApproach(this));
        this.tasks.addTask(3, new EntityAIRiderCircle(this));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        //this.tasks.addTask(3, new EntityAIWeaponSwapping(this));
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        /* Schedule the mainhand weapon change, so the combat AI tasks do not get altered in the middle of the AI tasks being ran. */
        if (this.sheduledMainhandSwap)
        {
            this.sheduledMainhandSwap = false;
            this.swingArm(EnumHand.MAIN_HAND);
            this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1, 1);
            this.getNavigator().clearPath();
            this.swapWeapon();
        }
    }

    /**
     * Horsemen ignore infighting
     */
    @Override
    public void setAttackTarget(@Nullable EntityLivingBase attackTarget) {
        if (attackTarget == null || attackTarget.isDead) {
            super.setAttackTarget(attackTarget);
            return;
        }
        if (attackTarget.getClass() == this.getClass()) return;
        super.setAttackTarget(attackTarget);
    }

    @Override
    public void setCombatTask() {
        //super.setCombatTask();
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        if (!(livingdata instanceof HorseTrapSpawnData)) {
            EntitySkeletonHorse horse = new EntitySkeletonHorse(world);
            horse.setPosition(posX, posY, posZ);
            world.spawnEntity(horse);
            startRiding(horse);
        }
        return super.onInitialSpawn(difficulty, livingdata);
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

    public int getCirclingAngle() {
        return circlingAngle;
    }

    public void setCirclingAngle(int angle) {
        circlingAngle = MathHelper.wrapDegrees(angle);
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

    public static class HorseTrapSpawnData implements IEntityLivingData {}

    /** An INCREDIBLY basic Ai Task for swapping held items using a randomized timer. */
    public static class EntityAIWeaponSwapping extends EntityAIBase
    {
        protected final EntitySkeletonRider rider;

        protected int swapCooldown;
        protected int nextSwapTime;

        public EntityAIWeaponSwapping(EntitySkeletonRider rider)
        {
            this.rider = rider;
            this.setMutexBits(0);
        }

        @Override
        public boolean shouldExecute() { return rider.getAttackTarget() != null; }

        @Override
        public boolean shouldContinueExecuting() { return shouldExecute(); }

        @Override
        public void startExecuting() { resetSwapTimer(); }

        @Override
        public void updateTask()
        {
            if (--swapCooldown <= 0)
            {
                rider.sheduledMainhandSwap = true;
                resetSwapTimer();
            }
        }

        protected void resetSwapTimer()
        {
            nextSwapTime = 100 + rider.getRNG().nextInt(80);
            swapCooldown = nextSwapTime;
        }
    }

    public static class EntityAIRiderApproach extends EntityAIBase {

        protected final EntitySkeletonRider rider;

        public EntityAIRiderApproach(EntitySkeletonRider rider) {
            this.rider = rider;
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            return rider.getAttackTarget() != null && rider.isRiding() && rider.getDistanceSq(rider.getAttackTarget()) >= 49;
        }

        @Override
        public void resetTask() {
            super.resetTask();
            rider.getNavigator().clearPath();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = rider.getAttackTarget();
            rider.getNavigator().tryMoveToEntityLiving(target, 10);
        }

    }

    public static class EntityAIRiderCircle extends EntityAIBase {

        protected final EntitySkeletonRider rider;
        private Vec3d nextPos;
        private int seeTime;
        private int attackTime = -1;

        public EntityAIRiderCircle(EntitySkeletonRider rider) {
            this.rider = rider;
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            return rider.getAttackTarget() != null && rider.isRiding() && rider.getDistanceSq(rider.getAttackTarget()) <= 49 &&
                    rider.getHeldItemMainhand().getItem() instanceof ItemBow;
        }

        @Override
        public boolean isInterruptible() {
            return false;
        }

        @Override
        public void startExecuting() {
            EntityLivingBase target = rider.getAttackTarget();
            nextPos = new Vec3d(target.posX, target.posY, target.posZ)
                    .add(DirectionUtils.getDirectionVecXZDegrees(rider.circlingAngle).scale(6));
            seeTime = 0;
            attackTime = -1;
        }

        @Override
        public void updateTask() {
            if (rider.ticksExisted % 20 == 0) findNext();
            rider.getNavigator().tryMoveToXYZ(nextPos.x, nextPos.y, nextPos.z, 1);
            rider.getLookHelper().setLookPosition(nextPos.x, nextPos.y, nextPos.z, 30, 30);
            boolean canSee = rider.getEntitySenses().canSee(rider.getAttackTarget());
            if (!canSee) {
                if (seeTime > 0) seeTime = 0;
                seeTime--;
            } else seeTime++;
            if (rider.isHandActive()) {
                if (seeTime < -60 || rider.getDistance(nextPos.x, nextPos.y, nextPos.z) > 4) rider.resetActiveHand();
                int useCount = rider.getItemInUseCount();
                if (canSee && useCount > 20) {
                    rider.resetActiveHand();
                    rider.attackEntityWithRangedAttack(rider.getAttackTarget(), ItemBow.getArrowVelocity(useCount));
                    this.attackTime = 20;
                }
            } else if (attackTime-- <= 0 && seeTime >= -60) rider.setActiveHand(EnumHand.MAIN_HAND);
        }

        private void findNext() {
            rider.setCirclingAngle(rider.getCirclingAngle() - 45);
            EntityLivingBase target = rider.getAttackTarget();
            nextPos = new Vec3d(target.posX, target.posY, target.posZ)
                    .add(DirectionUtils.getDirectionVecXZDegrees(rider.circlingAngle).scale(6));
            //System.out.println(nextPos + "");
        }

    }

}
