package net.smileycorp.mounts.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.SpearRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow @Final private List<IResourcePack> defaultResourcePacks;

    @Inject(at = @At("HEAD"), method = "init")
    public void CW$getResourcePackFiles(CallbackInfo callback) {
        try {
            SpearRegistry.generateData();
            defaultResourcePacks.add(new FolderResourcePack(SpearRegistry.CONFIG_FOLDER));
        } catch (Exception e) {
            MountsLogger.logError("Failed loading config resources", e);
        }
    }

}
