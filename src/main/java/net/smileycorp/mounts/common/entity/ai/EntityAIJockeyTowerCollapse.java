package net.smileycorp.mounts.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Random;

public class EntityAIJockeyTowerCollapse extends EntityAIBase {

    private final EntityLiving entity;

    public EntityAIJockeyTowerCollapse(EntityLiving entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return !entity.isRiding();
    }

    @Override
    public void startExecuting() {
        Random rand = entity.getRNG();
        for (Entity entity : this.entity.getRecursivePassengers()) {
            entity.dismountRidingEntity();
            entity.motionX += rand.nextFloat() - 0.5f;
            entity.motionY += 0.3;
            entity.motionZ += rand.nextFloat() - 0.5f;
        }
    }

}
