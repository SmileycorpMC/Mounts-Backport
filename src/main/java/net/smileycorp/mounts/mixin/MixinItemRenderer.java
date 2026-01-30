package net.smileycorp.mounts.mixin;

import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Shadow private float equippedProgressMainHand;

    @Shadow private float prevEquippedProgressMainHand;

    //store the players pos before movement is handled in a capability
    //for some reason vanilla player movement is
    @Inject(at = @At("TAIL"), method = "updateEquippedItem")
    public void mounts$updateEquippedItem(CallbackInfo ci) {
        //if ()
       //equippedProgressMainHand = 1;
       // prevEquippedProgressMainHand = 1;
    }

}
