package net.smileycorp.mounts.mixin;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.client.animation.AnimationsSpear;
import net.smileycorp.mounts.client.animation.MountsPlayerAnimationMethods;
import net.smileycorp.mounts.common.capabilities.CapabilityHelperUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public abstract class MixinModelBiped extends ModelBase {
    
    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;
    @Shadow
    public ModelBiped.ArmPose leftArmPose;
    @Shadow
    public ModelBiped.ArmPose rightArmPose;
    @Shadow
    public ModelRenderer bipedHead;

    @Shadow public ModelRenderer bipedBody;

    @Inject(method = "setRotationAngles", at = @At("HEAD"))
    public void deeperdepths$setRotationAngles$HEAD(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) entity;
        if (!(living.getHeldItemMainhand().getItem() instanceof ItemSpear)) return;
        swingProgress = 0;
        if (living.getPrimaryHand() == EnumHandSide.LEFT) leftArmPose = ModelBiped.ArmPose.EMPTY;
        else rightArmPose = ModelBiped.ArmPose.EMPTY;
    }
    
    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void deeperdepths$setRotationAngles$TAIL(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci)
    {
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) entity;

        EnumHandSide spearHandSide = MountsPlayerAnimationMethods.getHandSide(living, ItemSpear.class);
        if (spearHandSide == null) return;

        /* If an animation is currently playing. */
        boolean busyAnimating = false;
        ModelBiped model = (ModelBiped)(Object)this;
        float partialTicks = ageInTicks - entity.ticksExisted;

        EnumHand spearHand = spearHandSide == EnumHandSide.RIGHT && living.getPrimaryHand() == EnumHandSide.RIGHT ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;

        Item heldItem = living.getHeldItemMainhand().getItem();
        if (spearHandSide != living.getPrimaryHand()) heldItem = living.getHeldItemOffhand().getItem();

        SpearDefinition spearDef = ((ItemSpear) heldItem).getDefinition();

        /* Only Players use the custom animation durations. */
        if (living instanceof EntityPlayer)
        {
            if (CapabilityHelperUtil.isPlayerCustomSwingAnimating((EntityPlayer)living))
            {
                float customSwing = CapabilityHelperUtil.getPlayerCustomSwingAnimProgress((EntityPlayer)living, partialTicks);

                if (living.swingingHand == spearHand)
                {
                    AnimationsSpear.preformSpearArmRotations3edPerson(living, model, ageInTicks, customSwing, netHeadYaw, headPitch, spearHandSide, spearDef, true);
                    busyAnimating = true;
                }
            }
        }

        if (!busyAnimating && living.swingingHand == spearHand)
        { AnimationsSpear.preformSpearArmRotations3edPerson(living, model, ageInTicks, model.swingProgress, netHeadYaw, headPitch, spearHandSide, spearDef); }
    }
    
}
