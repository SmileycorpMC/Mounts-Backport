package net.smileycorp.mounts.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.client.ClientProxy;
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
    public void deeperdepths$setRotationAngles$TAIL(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) entity;
        if (!(living.getHeldItemMainhand().getItem() instanceof ItemSpear)) return;
        boolean isRight = living.getPrimaryHand() == EnumHandSide.RIGHT;
        ModelRenderer arm = isRight ? bipedRightArm : bipedLeftArm;
        arm.rotateAngleY = -0.1f * (isRight ? 1 : -1) + bipedHead.rotateAngleY;
        arm.rotateAngleX = (float) -(Math.PI/2) + bipedHead.rotateAngleX + 0.8f;
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isElytraFlying()) arm.rotateAngleX -= 0.9599311f;
        swingProgress = ((EntityLivingBase) entity).getSwingProgress(Minecraft.getMinecraft().getRenderPartialTicks());
        //System.out.println(swingProgress);
        bipedRightArm.rotateAngleY = bipedRightArm.rotateAngleY - bipedBody.rotateAngleY;
        bipedLeftArm.rotateAngleY = bipedLeftArm.rotateAngleY - bipedBody.rotateAngleY;
        bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX - bipedBody.rotateAngleZ;
        ClientProxy.animateSpearSwing(arm, swingProgress);
    }
    
}
