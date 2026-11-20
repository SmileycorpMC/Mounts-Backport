package net.smileycorp.mounts;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.smileycorp.mounts.config.FixesConfig;

public class MountsControlPathfinding {


    public static boolean updateEntityActionState$isRiding(EntityLiving instance, Operation<Boolean> original) {
        if (!FixesConfig.mountsControlOwnPathing) return original.call(instance);
        Entity entity = instance.getRidingEntity();
        if (!(entity instanceof EntityLiving)) return false;
        if (instance != entity.getControllingPassenger()) return false;
        /*entityLiving mount = (EntityLiving) entity;
        PathNavigate navigator = instance.getNavigator();
        Path path = navigator.getPath();
        if (path == null) {
            mount.getNavigator().setPath(null, 1);
            return false;
        }
        PathPoint point = path.getFinalPathPoint();
        Path mountPath = mount.getNavigator().getPath();
        if (mountPath != null && Objects.equals(mountPath.getFinalPathPoint(), point)) return false;
        mount.getNavigator().setPath(point == null ? path : mount.getNavigator().getPathToXYZ(point.x, point.y, point.z), navigator.speed);*/
        return false;
    }

    public static void updateEntityActionState$updateNavigator(EntityLiving entity, PathNavigate navigator) {
        getPathNavigate(entity, navigator).onUpdateNavigation();
    }

    public static PathNavigate getPathNavigate(EntityLiving entity, PathNavigate navigator) {
        Entity mount = entity.getRidingEntity();
        if (!(mount instanceof EntityLiving)) return navigator;
        if (entity != mount.getControllingPassenger()) return navigator;
        return ((EntityLiving) mount).getNavigator();
    }

}
