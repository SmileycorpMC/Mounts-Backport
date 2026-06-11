package net.smileycorp.mounts.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smileycorp.mounts.api.ItemSpear;

public class EntityAIAttackSpear extends EntityAIBase
{
    World world;
    protected EntityCreature attacker;
    protected EntityLivingBase target;
    double speedTowardsTarget;
    double speedRetreating;

    protected int retreatTimer = 0;

    public EntityAIAttackSpear(EntityCreature creature, double speedIn, double retreatSpeedIn)
    {
        this.attacker = creature;
        this.world = creature.world;
        this.speedTowardsTarget = speedIn;
        this.speedRetreating = retreatSpeedIn;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        ItemStack stack = attacker.getHeldItemMainhand();
        return attacker.getAttackTarget() != null && stack.getItem() instanceof ItemSpear;
    }

    @Override
    public boolean shouldContinueExecuting()
    { return target != null && target.isEntityAlive() && shouldExecute(); }

    @Override
    public void startExecuting()
    {
        this.target = attacker.getAttackTarget();
        this.retreatTimer = 0;
    }

    @Override
    public void resetTask()
    {
        attacker.stopActiveHand();
        this.target = null;
    }

    @Override
    public void updateTask()
    {
        if (target == null) return;

        double distSq = attacker.getDistanceSq(target);
        if (retreatTimer > 0)
        {
            attacker.resetActiveHand();
            retreatTimer--;

            if(attacker.ticksExisted % 10 == 0)
            {
                Vec3d away = RandomPositionGenerator.findRandomTargetBlockAwayFrom(attacker, 10, 5, new Vec3d(target.posX, target.posY, target.posZ) );

                if (away != null) attacker.getNavigator().tryMoveToXYZ(away.x, away.y, away.z, this.speedRetreating);
            }

            return;
        }
        /* If too close to the Player, go into retreat. */
        if (distSq <= 8)
        {
            retreatTimer = 25;
            attacker.getNavigator().clearPath();
            return;
        }

        if(attacker.ticksExisted % 10 == 0) attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget);
        attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

        if (distSq <= 128) attacker.setActiveHand(EnumHand.MAIN_HAND);
    }
}