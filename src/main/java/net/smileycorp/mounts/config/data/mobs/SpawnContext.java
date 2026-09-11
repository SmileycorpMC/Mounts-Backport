package net.smileycorp.mounts.config.data.mobs;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.Random;

public class SpawnContext {

    private final EntityLiving entity;
    private DifficultyInstance difficulty;
    private State state = State.ACTIVE;

    public SpawnContext(EntityLiving entity) {
        this.entity = entity;
    }

    public SpawnContext(EntityLiving entity, DifficultyInstance difficulty) {
        this.entity = entity;
        this.difficulty = difficulty;
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

    public DifficultyInstance getDifficulty() {
        if (difficulty == null) difficulty = entity.world.getDifficultyForLocation(entity.getPosition());
        return difficulty;
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
