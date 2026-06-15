package net.smileycorp.mounts.mixin;

import net.minecraft.world.WorldServer;
import net.smileycorp.mounts.config.MountsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(WorldServer.class)
public class MixinWorldServer {

    @ModifyConstant(method = "updateBlocks", constant = @Constant(doubleValue = 0.01D))
    public double mounts$updateBlocks$double(double d) {
        return MountsConfig.horseTrapSpawnChance;
    }

}
