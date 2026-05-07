package net.smileycorp.mounts.client.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class MountsPlayerAnimationMethods
{
    public static void applyFirstPersonBaseTransform(boolean left, float swing)
    {
        float side = left ? -1F : 1F;

        GlStateManager.translate(side * 0.56F, -0.52F, -0.72F);
        GlStateManager.rotate(side, 0.0F, 1.0F, 0.0F);

        float f  = -0.4F * MathHelper.sin(MathHelper.sqrt(swing) * (float)Math.PI);
        float f1 =  0.2F * MathHelper.sin(MathHelper.sqrt(swing) * ((float)Math.PI * 2F));
        float f2 = -0.2F * MathHelper.sin(swing * (float)Math.PI);

        GlStateManager.translate(side * f, f1, f2);

        float wowza = MathHelper.sin(swing * swing * (float)Math.PI);
        GlStateManager.rotate(side * (45.0F + wowza * -20.0F), 0.0F, 1.0F, 0.0F);

        float predictive = MathHelper.sin(MathHelper.sqrt(swing) * (float)Math.PI);
        GlStateManager.rotate(side * predictive * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(predictive * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(side * -45.0F, 0.0F, 1.0F, 0.0F);
    }


    /** Gets a usable version of headYaw when applying 3d person player animations. */
    public static float getEntityHeadYaw(EntityLivingBase entity, float ageInTicks, float clamp)
    {
        float partialTicks = ageInTicks - entity.ticksExisted;
        float headYaw = entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * partialTicks;
        float bodyYaw = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;

        float netYaw = headYaw - bodyYaw;
        netYaw = MathHelper.wrapDegrees(netYaw);
        netYaw = MathHelper.clamp(netYaw, -clamp, clamp);

        return netYaw * 0.017453292F;
    }


    /** Resets the positions and rotations of the player model. */
    public static void preformPlayerAnimReset(ModelBiped model)
    {
        model.bipedRightArm.rotationPointX = -5F;
        model.bipedRightArm.rotationPointY = 2F;
        model.bipedRightArm.rotationPointZ = 0F;

        model.bipedLeftArm.rotationPointX = 5F;
        model.bipedLeftArm.rotationPointY = 2F;
        model.bipedLeftArm.rotationPointZ = 0F;

        model.bipedBody.rotationPointX = 0F;
        model.bipedBody.rotationPointY = 0F;
        model.bipedBody.rotationPointZ = 0F;

        model.bipedHead.rotationPointX = 0F;
        model.bipedHead.rotationPointY = 0F;
        model.bipedHead.rotationPointZ = 0F;

        model.bipedBody.rotateAngleZ = 0F;
    }

    /** Created a segment (0F -> 1.0F float) within the specified time of the animation. Like pseudo keyframes? */
    static float segmentAnimationTime(float animTimeIn, float segStart, float segEnd)
    { return MathHelper.clamp((animTimeIn - segStart) / (segEnd - segStart), 0.0F, 1.0F); }

    /** This simple helper gives me the hand the desired item instance is being held in (and doubles to make sure it is held at all!)
     * Seriously, saves a lot of copied code between animations.
     * */
    public static EnumHandSide getHandSide(EntityLivingBase living, Class<? extends Item> item)
    {
        ItemStack main = living.getHeldItemMainhand();
        ItemStack off = living.getHeldItemOffhand();

        if (!main.isEmpty() && item.isInstance(main.getItem()))
            return living.getPrimaryHand();

        if (!off.isEmpty() && item.isInstance(off.getItem()))
            return living.getPrimaryHand().opposite();

        return null;
    }
}