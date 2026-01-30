package net.smileycorp.mounts.common.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.smileycorp.mounts.common.Mounts;
import net.smileycorp.mounts.config.MountsConfig;

import java.util.concurrent.TimeUnit;

public class EntityAIFindMount extends EntityAIBase {

    private final EntityLiving entity;
    private int checks = 20;
    private EntityLiving target;

    public EntityAIFindMount(EntityLiving entity) {
        this.entity = entity;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (checks-- <= 0) {
            removeTask();
            return false;
        }
        if (target != null) {
            if (MountsConfig.canBabyZombieMount(target) &! (entity.getDistanceSq(entity) > 1024)) return true;
            target = null;
        }
        for (Entity e : entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getEntityBoundingBox().grow(32, 4, 32))) {
            if (!(e instanceof EntityLiving)) continue;
            if (!MountsConfig.canBabyZombieMount(entity)) continue;
            double dis = e.getDistanceSq(entity);
            if (dis <= 1024) continue;
            if (target == null) target = (EntityLiving) e;
            else if (target.getDistanceSq(entity) > dis) target = (EntityLiving) e;
        }
        return target != null;
    }

    @Override
    public void startExecuting() {
        entity.getNavigator().tryMoveToEntityLiving(target, 1);
    }

    @Override
    public void updateTask() {
        if (target.getDistanceSq(entity) > 25) return;
        entity.startRiding(target, true);
        removeTask();
    }

    @Override
    public void resetTask() {
        target = null;
    }

    private void removeTask() {
        Mounts.DELAYED_THREAD_EXECUTOR.schedule(() -> entity.tasks.removeTask(this), 20, TimeUnit.MILLISECONDS);
    }

}
