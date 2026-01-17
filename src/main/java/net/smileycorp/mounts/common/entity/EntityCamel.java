package net.smileycorp.mounts.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;

import javax.annotation.Nullable;
import java.util.List;

public class EntityCamel extends EntityAnimal
{
    private float accel = 0.0F;

    private static final DataParameter<Integer> ANIMSTATE = EntityDataManager.createKey(EntityCamel.class, DataSerializers.VARINT);
    /* Primarily handles the Dashing Animation. */
    private static final DataParameter<Integer> DASHING = EntityDataManager.createKey(EntityCamel.class, DataSerializers.VARINT);
    private float animTransSpeed = 0.4F;
    private float animationTime;
    private float prevAnimationTime;

    /** USed when the Camel is preforming a sit (wraps animation logic)*/
    public boolean sitting;
    public int dashCooldown;


    public EntityCamel(World worldIn)
    {
        super(worldIn);
        this.setSize(1.7F, 2.375F);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(ANIMSTATE, 0);
        this.dataManager.register(DASHING, 0);
    }

    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 2.0D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.WHEAT, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D, 1.0F));

        this.tasks.addTask(5, new EntityCamel.AICamelSitRandomly(this));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
    }

    public void onLivingUpdate()
    {
        prevAnimationTime = animationTime;

        animationTime = Math.min(1.0F, animationTime + 0.02F);

        /* Upon concluding the animation, this stitches into other ones. */
        if (animationTime >= 1.0F)
        {
            if (getAnimState() == AnimState.SIT_START) setAnimState(AnimState.SIT);
            else if (getAnimState() == AnimState.SIT_END) setAnimState(AnimState.NONE);
        }

        if (this.isInWater()) this.standUp(true);
        if (this.hurtTime > 0) this.standUp(true);


        if (this.getDashing() > 0) this.setDashing(this.getDashing() - 1);
        if (this.dashCooldown > 0) --this.dashCooldown;

        super.onLivingUpdate();
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        if (!this.world.isRemote)
        {
            if (player.isSneaking())
            {

                return true;
            }
            else
            {
                /* Temporary until I can elarn how to properly sync animations with adjustments made within the riding method. */
                this.standUp(false);
                player.startRiding(this);
                return super.processInteract(player, hand);
            }
        }
        return super.processInteract(player, hand);
    }

    public float getEyeHeight() { return this.height * 0.9F; }

    public double getMountedYOffset()
    {
        double standY = 0.75D * this.height;
        double sitY = 0.35D * this.height;

        double t = this.animationTime;
        switch (getAnimState())
        {
            case SIT_START:
                return (standY + (sitY - standY) * t);
            case SIT_END:
                return (sitY + (standY - sitY) * t);
            case SIT:
                return sitY;
            default:
                return standY;
        }
    }

    public void updatePassenger(Entity passenger)
    {
        if (!this.isPassenger(passenger)) return;

        int i = this.getPassengers().indexOf(passenger);

        double forwardOffset = i == 1 ? -0.6D : 0.5D;
        double yOffset = this.getMountedYOffset() + passenger.getYOffset();

        Vec3d offset = new Vec3d(0, 0.0D, forwardOffset).rotateYaw(-this.rotationYaw * 0.017453292F);

        passenger.setPosition( this.posX + offset.x, this.posY + yOffset, this.posZ + offset.z);
    }

    public void standUp(boolean skipAnimation)
    {
        if (skipAnimation) this.setAnimState(AnimState.NONE);
        else this.setAnimState(AnimState.SIT_END);
        this.sitting = false;
    }

    public void preformDash(Vec3d direction)
    {
        //this.playSound(getDashSound(), 1.0F, 1.0F);
        if (!this.world.isRemote) return;
        if (this.canBePushed())
        {
            this.motionX = direction.x;
            this.motionY = direction.y;
            this.motionZ = direction.z;

            this.motionY += 0.4F;
            this.velocityChanged = true;
        }
    }

    public void travel(float strafe, float vertical, float forward)
    {
        if (this.isBeingRidden() && this.canBeSteered())
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.getControllingPassenger();
            this.stepHeight = 1.0F;
            this.rotationYaw = entitylivingbase.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = entitylivingbase.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;
            strafe = entitylivingbase.moveStrafing * 0.5F;
            forward = entitylivingbase.moveForward;

            if (this.canPassengerSteer())
            {
                if (this.getAnimState() != AnimState.NONE)
                {
                    //if (forward > 0 && this.getAnimState() == AnimState.SIT) standUp(false);
                    this.setAIMoveSpeed(0);
                    super.travel(strafe, vertical, forward);
                    return;
                }

                /* This is the Dash behavior. */
                if (entitylivingbase instanceof EntityPlayer)
                {
                    EntityPlayer player = (EntityPlayer)entitylivingbase;

                    if (player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null))
                    {
                        CapabilitySpearMovement.ICapabilityMountsPlayerInfo capCharge = player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null);

                        if (!capCharge.getIsSpaceHeld() && capCharge.getSpaceHeldTime() > 0 && this.dashCooldown <= 0)
                        {
                            Vec3d moveVec = player.getLookVec().scale((capCharge.getSpaceHeldTime()) * 2);
                            preformDash(moveVec);

                            capCharge.setSpaceHeldTime(0);

                            this.setDashing(120);
                            this.dashCooldown = 20;
                        }
                    }
                }

                float f = (float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 0.225F;
                float accelIncrease = 0.1F;
                float accelDecay    = 0.1F;
                float maxAccel      = 2.0F;

                if (forward > 0)
                {
                    accel += accelIncrease;
                    if (accel > maxAccel) accel = maxAccel;
                }
                else
                {
                    accel -= accelDecay;
                    if (accel < 0.5F) accel = 0.5F;
                }

                this.setAIMoveSpeed(f * accel);
                super.travel(strafe, vertical, forward);
            }
            else
            {
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            /* This section related to LimbSwing is REQUIRED for the Client and Server to stay synced! CANNOT BE ALTERED! */
            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
            if (f1 > 1.0F) { f1 = 1.0F; }

            this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else
        {
            this.stepHeight = 0.5F;
            this.jumpMovementFactor = 0.02F;
            super.travel(strafe, vertical, forward);
        }
    }


    /* First Player gets Control. */
    public Entity getControllingPassenger()
    {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean canBeSteered()
    { return this.getControllingPassenger() instanceof EntityLivingBase; }

    protected boolean canFitPassenger(Entity passenger)
    { return this.getPassengers().size() < 2; }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        AxisAlignedBB bb = this.getEntityBoundingBox();

        switch (this.getAnimState())
        {
            default:
                this.setSize(1.7F, 2.375F);
                this.height = 2.375F * (this.isChild() ? 0.5F : 1F);
                this.setEntityBoundingBox( new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.minX + this.width, bb.minY + this.height, bb.minZ + this.width));
                break;
            case SIT_START:
            case SIT:
                this.setSize(1.7F, 2.375F * 0.5F);
                this.height = 2.375F * 0.5F * (this.isChild() ? 0.5F : 1F);
                this.setEntityBoundingBox( new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.minX + this.width, bb.minY + this.height, bb.minZ + this.width));
                break;
        }


        if (ANIMSTATE.equals(key))
        {
            this.animationTime = 0.0F;
            this.prevAnimationTime = 0.0F;
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    protected void collideWithEntity(Entity entityIn)
    {
        if (!this.isRidingSameEntity(entityIn) && !entityIn.noClip && !this.noClip)
        {
            double d0 = entityIn.posX - this.posX;
            double d1 = entityIn.posZ - this.posZ;
            double d2 = MathHelper.absMax(d0, d1);

            if (d2 >= 0.009999999776482582D)
            {
                d2 = (double)MathHelper.sqrt(d2);
                d0 = d0 / d2;
                d1 = d1 / d2;
                double d3 = 1.0D / d2;

                if (d3 > 1.0D)
                {
                    d3 = 1.0D;
                }

                d0 = d0 * d3;
                d1 = d1 * d3;
                d0 = d0 * 0.05000000074505806D;
                d1 = d1 * 0.05000000074505806D;
                d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                d1 = d1 * (double)(1.0F - this.entityCollisionReduction);


                if (!entityIn.isBeingRidden())
                { entityIn.addVelocity(d0, 0.0D, d1); }
            }
        }
    }

    public boolean canBePushed() { return this.getAnimState() == AnimState.NONE; }

    // TODO: Move to a better spot, and make the 'shouldStand' method!
    public boolean shouldCamelSit()
    { return this.onGround && !this.isInWater() && !this.getLeashed() && !this.isBeingRidden(); }

    @Nullable
    public EntityAgeable createChild(EntityAgeable ageable) { return new EntityCamel(this.world); }

    @SideOnly(Side.CLIENT)
    public float getClientAnimationTime(float partialTick)
    { return this.prevAnimationTime + (this.animationTime - this.prevAnimationTime) * partialTick;  }

    public enum AnimState
    {
        NONE,
        SIT_START,
        SIT,
        SIT_END;
    }

    public void setAnimState(EntityCamel.AnimState pose) { setAnimState(pose, true); }

    public void setAnimState(EntityCamel.AnimState pose, boolean animateFromPrevious)
    {
        int newOrdinal = pose.ordinal();

        this.dataManager.set(ANIMSTATE, newOrdinal);
        this.prevAnimationTime = 0.0F;
        this.animationTime = 0.0F;
    }

    public AnimState getAnimState()
    { return AnimState.values()[this.dataManager.get(ANIMSTATE)]; }

    public int getDashing() { return this.dataManager.get(DASHING); }
    public void setDashing(int state) { this.dataManager.set(DASHING, state); }

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("AnimationState", this.getAnimState().ordinal());
    }

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setAnimState(EntityCamel.AnimState.values()[compound.getInteger("AnimationState")]);
    }

    /**
     * AI for Clams to randomly Open and Close
     * */
    class AICamelSitRandomly extends EntityAIBase
    {
        private final EntityCamel camel;
        private int cooldown;


        private AICamelSitRandomly(EntityCamel camelIn)
        { this.camel = camelIn; }

        public boolean shouldExecute()
        {
            if (!camel.shouldCamelSit()) return false;
            if (this.cooldown > 0)
            {
                this.cooldown--;
                return false;
            }

            return camel.rand.nextInt(10) == 0;
        }

        public void startExecuting()
        {
            if (!camel.sitting && getAnimState() == AnimState.NONE)
            {
                camel.sitting = true;
                setAnimState(AnimState.SIT_START);
            }
            else if (camel.sitting && getAnimState() == AnimState.SIT) { setAnimState(AnimState.SIT_END); }

            cooldown = 10;
        }
    }
}