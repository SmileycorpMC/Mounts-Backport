package net.smileycorp.mounts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.smileycorp.mounts.MountsControlPathfinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        return MountsControlPathfinding.updateEntityActionState$isRiding(instance, original);
    }

    //fixes bug where multi passenger entities are controlled by both passengers
    //this legitimately took me 3 and a half hours to realise that's why the ai was bugging
    //and another 3 hours to find this method that was causing it
    //in fact all the code called by this method can be skipped, vanilla really shouldn't be doing any of this
    //let the ridden entity calculate it's pathing, should fix issues with large mob hitboxes getting stuck too
    @WrapOperation(at= @At(value = "INVOKE", target = "Lnet/minecraft/pathfinding/PathNavigate;onUpdateNavigation()V"), method = "updateEntityActionState")
    public void mounts$updateEntityActionState$onUpdateNavigation(PathNavigate instance, Operation<Void> original) {
        MountsControlPathfinding.updateEntityActionState$updateNavigator((EntityLiving)(EntityLivingBase)this, instance);
    }

    @Inject(at= @At(value = "RETURN"), method = "getNavigator", cancellable = true)
    public void mounts$getNavigator(CallbackInfoReturnable<PathNavigate> cir) {
        cir.setReturnValue(MountsControlPathfinding.getPathNavigate((EntityLiving)(EntityLivingBase)this, cir.getReturnValue()));
    }

}
