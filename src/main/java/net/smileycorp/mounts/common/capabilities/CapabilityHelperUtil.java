package net.smileycorp.mounts.common.capabilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class CapabilityHelperUtil
{
    public static boolean isPlayerCustomSwingAnimating(EntityPlayer playerIn)
    {
        CapabilitySpearAnimation.ICapabilityAnimations capAnim = getAnimationCapability(playerIn);
        if (capAnim == null) return false;

        return capAnim.getCustomSwingEndTime() > playerIn.ticksExisted;
    }

    public static float getPlayerCustomSwingAnimProgress(EntityPlayer playerIn, float particalTick)
    {
        CapabilitySpearAnimation.ICapabilityAnimations capAnim = getAnimationCapability(playerIn);
        if (capAnim == null) return 0;

        float duration = capAnim.getCustomSwingEndTime() - capAnim.getCustomSwingStartTime();
        float elapsed = (playerIn.ticksExisted + particalTick) - capAnim.getCustomSwingStartTime();

        return MathHelper.clamp(elapsed / duration, 0F, 1F);
    }

    public static float getSpearRecoilAnimationProgress(EntityLivingBase userIn, float partialTick)
    {
        CapabilitySpearAnimation.ICapabilityAnimations capAnim = getAnimationCapability(userIn);
        if (capAnim == null) return 0;
        int start = capAnim.getSpearRecoilStartTime();
        float elapsed = (userIn.ticksExisted + partialTick) - start;
        float duration = 6.0F;
        return MathHelper.clamp(elapsed / duration, 0F, 1F);
    }

    public static CapabilitySpearAnimation.ICapabilityAnimations getAnimationCapability(EntityLivingBase userIn)
    {
        if (userIn.hasCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null))
        { return userIn.getCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null); }

        return null;
    }
}