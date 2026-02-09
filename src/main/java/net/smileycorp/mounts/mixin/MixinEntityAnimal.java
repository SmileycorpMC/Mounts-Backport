package net.smileycorp.mounts.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAnimal.class)
public abstract class MixinEntityAnimal extends EntityAgeable {

    @Shadow protected Block spawnableBlock;

    public MixinEntityAnimal(World world) {
        super(world);
    }

    @Inject(at=@At("HEAD"), method = "canDespawn", cancellable = true)
    public void mounts$canDespawn(CallbackInfoReturnable<Boolean> callback) {
        if ((Object)this instanceof EntityZombieHorse) callback.setReturnValue(true);
    }

    @Inject(at=@At("HEAD"), method = "getCanSpawnHere", cancellable = true)
    public void mounts$getCanSpawnHere(CallbackInfoReturnable<Boolean> callback) {
        if (!(((EntityAgeable)this) instanceof EntityZombieHorse)) return;
        BlockPos pos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getEntityBoundingBox().minY), MathHelper.floor(posZ));
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL || world.getBlockState(pos.down()).getBlock() != spawnableBlock
                || world.getLightFor(EnumSkyBlock.SKY, pos) <= rand.nextInt(32) |! super.getCanSpawnHere()) {
            callback.setReturnValue(false);
            return;
        }
        int blockLight = world.getLightFromNeighbors(pos);
        if (world.isThundering()) {
            int skylight = this.world.getSkylightSubtracted();
            world.setSkylightSubtracted(10);
            blockLight = world.getLightFromNeighbors(pos);
            world.setSkylightSubtracted(skylight);
        }
        callback.setReturnValue(blockLight <= rand.nextInt(8));

    }

}
