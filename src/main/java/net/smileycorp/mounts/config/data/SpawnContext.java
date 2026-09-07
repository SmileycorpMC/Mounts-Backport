package net.smileycorp.mounts.config.data;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class SpawnContext {

    private final EntityLiving entity;
    private State state = State.ACTIVE;

    public SpawnContext(EntityLiving entity) {
        this.entity = entity;
    }

    public EntityLiving getEntity() {
        return entity;
    }

    public World getWorld() {
        return entity.world;
    }

    public Random getRandom() {
        return entity.getRNG();
    }

    public BlockPos getPos() {
        return entity.getPosition();
    }

    public void resetState() {
        if (state == State.BROKEN) state = State.ACTIVE;
    }

    public void breakScript() {
        if (state == State.ACTIVE) state = State.BROKEN;
    }

    public void returnScript() {
        state = State.RETURNED;
    }

    public boolean isBroken() {
        return state != State.ACTIVE;
    }

    private enum State {
        ACTIVE,
        BROKEN,
        RETURNED
    }

}
