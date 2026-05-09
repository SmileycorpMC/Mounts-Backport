package net.smileycorp.mounts.client.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.client.MountsClientEvents;
import net.smileycorp.mounts.common.capabilities.CapabilityHelperUtil;

public class AnimationsSpear
{
    /**
     * First Person
     * */
    public static void preformSpearSwingItemRotations1stPerson(EntityPlayer player, float swing, float partialTicks, EnumHandSide hand)
    {
        float handAdjustment = hand == EnumHandSide.RIGHT ? 1 : -1;
        float cooldown = player.getCooledAttackStrength(partialTicks);

        float windUp = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.0F, 0.1F);
        float thrust = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.125F, 0.175F);
        float toNeutral = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.3F, 0.99F);

        GlStateManager.translate(0,(windUp * 0.4F) + (toNeutral * -0.4F) + (toNeutral * (-1 + (cooldown * 1))),(windUp * 0.2F) + (thrust * -1.1) + (toNeutral * 0.8F));

        GlStateManager.rotate((windUp * -30.0F) + (toNeutral * 30.0F), 1.0F, 0.0F, 0);
        GlStateManager.rotate((thrust * 30.0F) + (toNeutral * -30.0F), 0.0F, handAdjustment, 0);

        if (swing >= 1) MountsClientEvents.swingSpear = false;
    }

    public static void preformSpearUseItemRotations1stPerson(EntityPlayer player, float swing, float partialTicks, EnumHandSide hand)
    {
        float handAdjustment = hand == EnumHandSide.RIGHT ? 1 : -1;

        if (player.getItemInUseCount() > 0)
        {
            float totalUseTick = player.getItemInUseMaxCount() + partialTicks;
            float progress = Math.min(totalUseTick / 8F, 1F);

            GlStateManager.translate(progress * -0.25F * handAdjustment,progress * 0.5F,progress * 0.55F);
            GlStateManager.rotate(progress * -10.0F, 1.0F, 0.0F, 0);

            float shakeProgress = Math.min(totalUseTick / 300F, 1F);
            float shake = (MathHelper.cos(player.ticksExisted * 1.5F) * 0.005F) * (0.5F + shakeProgress);
            GlStateManager.translate(0, shake, 0);
        }
    }


    /**
     * Third Person Item
     * */
    public static void preformSpearSwingItemRotations3edPerson(EntityLivingBase living, EnumHandSide hand, float partialTicks, float swing, ModelBiped model)
    { preformSpearSwingItemRotations3edPerson(living, hand, partialTicks, swing, model, false); }

    public static void preformSpearSwingItemRotations3edPerson(EntityLivingBase living, EnumHandSide hand, float partialTicks, float swing, ModelBiped model, boolean useFullerTimings)
    {
        if (swing > 0 & (!useFullerTimings || swing < 0.5F))
        {
            float archBack1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.0F, 0.1F);
            float archForward1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.1F, 0.3F);
            float ending = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.6F, 1.0F);
            if (useFullerTimings)
            {
                archBack1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.0F, 0.025F);
                archForward1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.025F, 0.1F);
                ending = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.3F, 0.5F);
            }

            GlStateManager.translate(0, (archForward1 * -0.3F) + (ending * 0.3F),(archForward1 * -0.2F) + (ending * 0.2F));
            GlStateManager.rotate((archBack1 * 40) + (archForward1 * -125) + (ending * 85), 1, 0,0);
        }
    }

    public static void preformSpearUseItemRotations3edPerson(EntityLivingBase living, EnumHandSide hand, float partialTicks, float swing, ModelBiped model, SpearDefinition spearDef)
    { preformSpearUseItemRotations3edPerson(living, hand, partialTicks, swing, model, spearDef, false); }

    public static void preformSpearUseItemRotations3edPerson(EntityLivingBase living, EnumHandSide hand, float partialTicks, float swing, ModelBiped model, SpearDefinition spearDef, boolean useFullerTimings)
    {
        float totalUseTick = living.getItemInUseMaxCount() + partialTicks;
        float progress = Math.min(totalUseTick / 8F, 1F);

        int dismountEnd = spearDef.getChargeDismountDuration();
        float knockbackPhaseStart = MountsPlayerAnimationMethods.segmentAnimationTime(totalUseTick, dismountEnd, dismountEnd + 16);

        float recoilProgress = CapabilityHelperUtil.getSpearRecoilAnimationProgress(living, partialTicks);
        float rKey1 = MountsPlayerAnimationMethods.segmentAnimationTime(recoilProgress, 0F, 0.4F);
        float rKey2 = MountsPlayerAnimationMethods.segmentAnimationTime(recoilProgress, 0.4F, 1.0F);

        GlStateManager.translate((progress * -0.1F) - (knockbackPhaseStart * -0.1F), (progress * -0.1F) + (knockbackPhaseStart * -0.2F), 0);
        GlStateManager.translate(0, (rKey1 * -0.25F) + (rKey2 * 0.25F), (rKey1 * 0.5F) + (rKey2 * -0.5F));
        GlStateManager.rotate((progress * -70), 1, 0,0);
        GlStateManager.rotate((progress * 90) - (knockbackPhaseStart * 90), 0, 1,0);
    }


    /**
     * Third Person Player
     * */
    public static void preformSpearArmRotations3edPerson(Entity entityIn, ModelBiped model, float ageInTicks, float swing, float headYaw, float headPitch, EnumHandSide hand, SpearDefinition spearDef)
    { preformSpearArmRotations3edPerson(entityIn, model, ageInTicks, swing, headYaw, headPitch, hand, spearDef, false); }

    /** This is the animation for swinging the Throwing Spear. */
    public static void preformSpearArmRotations3edPerson(Entity entityIn, ModelBiped model, float ageInTicks, float swing, float headYaw, float headPitch, EnumHandSide hand, SpearDefinition spearDef, boolean useFullerTimings)
    {
        if (!(entityIn instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase)entityIn;

        boolean rightHanded = hand == EnumHandSide.RIGHT;
        ModelRenderer mainArm = hand == EnumHandSide.RIGHT ? model.bipedRightArm : model.bipedLeftArm;
        ModelRenderer offArm  = hand == EnumHandSide.RIGHT ? model.bipedLeftArm  : model.bipedRightArm;



        if (living.getItemInUseCount() > 0)
        {
            float totalUseTick = living.getItemInUseMaxCount() + (ageInTicks - entityIn.ticksExisted);
            int dismountEnd = spearDef.getChargeDismountDuration();
            int knockbackEnd = spearDef.getChargeKnockbackDuration();
            //int damageEnd = spearDef.getChargeDamageDuration();

            float ready1 = MountsPlayerAnimationMethods.segmentAnimationTime(totalUseTick, 0, 3);
            float ready2 = MountsPlayerAnimationMethods.segmentAnimationTime(totalUseTick, 3, 6);
            float ready3 = MountsPlayerAnimationMethods.segmentAnimationTime(totalUseTick, 6, 10);
            float knockbackPhaseStart = MountsPlayerAnimationMethods.segmentAnimationTime(totalUseTick, dismountEnd, dismountEnd + 16);
            float damagePhaseStart1 = MountsPlayerAnimationMethods.segmentAnimationTime(totalUseTick, knockbackEnd, knockbackEnd + 6);
            float damagePhaseStart2 = MountsPlayerAnimationMethods.segmentAnimationTime(totalUseTick, knockbackEnd + 6, knockbackEnd + 16);


            mainArm.rotateAngleX = -0.9F + (ready1 * -0.8F) + (ready2 * 0.8F) + (ready3 * -0.4F) + (damagePhaseStart1 * -0.3F)+ (damagePhaseStart2 * 0.7F) + (ready1 * model.bipedHead.rotateAngleX);

            float shake1 = MathHelper.sin(ageInTicks * 0.3F) * 0.1F;
            float shakeZ = MathHelper.cos(ageInTicks * 0.5F) * 0.1F;

            mainArm.rotateAngleX += shake1 * knockbackPhaseStart;
            mainArm.rotateAngleZ += shakeZ * knockbackPhaseStart;
        }
        else if (swing > 0 & (!useFullerTimings || swing < 0.5F))
        {
            float archBack1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.0F, 0.1F);
            float archForward1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.1F, 0.3F);
            float ending = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.6F, 1.0F);
            if (useFullerTimings)
            {
                archBack1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.0F, 0.025F);
                archForward1 = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.025F, 0.1F);
                ending = MountsPlayerAnimationMethods.segmentAnimationTime(swing, 0.3F, 0.5F);
            }

            model.bipedBody.rotateAngleY = (archBack1 * 0.15F) + (archForward1 * -0.3F) + (ending * 0.15F);

            if (entityIn.isSneaking()) model.bipedBody.rotateAngleY *= 0.5F;

            offArm.rotationPointZ += (archBack1 * -0.5) + (archForward1 * 1) + (ending * -0.5);

            mainArm.rotationPointZ += (archBack1 * 0.5) + (archForward1 * -1) + (ending * 0.5);

            mainArm.rotateAngleX = (-0.9F + (entityIn.isSneaking() ? 0.5F : 0)) + (archBack1 * 0.6F) + (archForward1 * -1.2F) + (ending * 0.6F) + ((archBack1 * model.bipedHead.rotateAngleX) + (ending * -model.bipedHead.rotateAngleX));
            mainArm.rotateAngleY = -0.1F;
            if (!rightHanded)
            {
                model.bipedBody.rotateAngleY *= -1;
                offArm.rotateAngleZ *= -1;
                mainArm.rotateAngleZ *= -1;
            }
        }
        else
        { mainArm.rotateAngleX = (-0.9F + (entityIn.isSneaking() ? 0.5F : 0)); }

        mainArm.rotateAngleY = -0.1F + (MountsPlayerAnimationMethods.getEntityHeadYaw(living, ageInTicks, 70));
    }
}