package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends EntityLivingBase {

    @Shadow @Final private NonNullList<ItemStack> inventoryArmor;

    @Shadow public abstract PathNavigate getNavigator();

    public MixinEntityLiving(World worldIn) {
        super(worldIn);
    }

    //fixes bug where multi passenger entities are controlled by both passengers
    //this legitimately took me 3 and a half hours to realise that's why the ai was bugging
    //and another 3 hours to find this method that was causing it
    //in fact all the code called by this method can be skipped, vanilla really shouldn't be doing any of this
    //let the ridden entity calculate it's pathing, should fix issues with large mob hitboxes getting stuck too
    @WrapOperation(at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;isRiding()Z"), method = "updateEntityActionState")
    public boolean mounts$updateEntityActionState$isRiding(EntityLiving instance, Operation<Boolean> original) {
        Entity entity = getRidingEntity();
        if (!(entity instanceof EntityLiving)) return false;
        if (this != entity.getControllingPassenger()) return false;
        EntityLiving living = (EntityLiving) entity;
        Path path = getNavigator().getPath();
        if (path == null) {
            living.getNavigator().setPath(null, 1.5);
            return false;
        }
        PathPoint point = path.getFinalPathPoint();
        living.getNavigator().setPath(point == null ? path : living.getNavigator().getPathToXYZ(point.x, point.y, point.z), 1.5);
        return false;
    }

}
