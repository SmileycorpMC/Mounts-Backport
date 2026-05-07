package net.smileycorp.mounts.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.client.animation.AnimationsSpear;
import net.smileycorp.mounts.common.capabilities.CapabilityHelperUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(LayerHeldItem.class)
public class MixinLayerHeldItem
{
    @Shadow
    @Final
    protected RenderLivingBase<?> livingEntityRenderer;

    @Inject( method = "renderHeldItem", at = @At("HEAD"), cancellable = true)
    public void renderItemHeld(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transforms, EnumHandSide hand, CallbackInfo callback)
    {
        if (entity == null || stack.isEmpty()) return;
        if (!(livingEntityRenderer.getMainModel() instanceof ModelBiped)) return;
        Item itemType = stack.getItem();
        if (!(itemType instanceof ItemSpear)) return;
        /* If everything else passed, move on to the full overrides! */
        callback.cancel();

        ModelBiped model = (ModelBiped) livingEntityRenderer.getMainModel();
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        /* First, all the normal translations. */
        GlStateManager.pushMatrix();

        if (entity.isSneaking())  GlStateManager.translate(0.0F, 0.2F, 0.0F);
        ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, hand);
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        boolean flag = hand == EnumHandSide.LEFT;
        GlStateManager.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);

        SpearDefinition spearDef = ((ItemSpear) itemType).getDefinition();

        /* Then, I take over with special transforms. */
        if (entity instanceof EntityPlayer && CapabilityHelperUtil.isPlayerCustomSwingAnimating((EntityPlayer)entity))
        {
            float fucker = CapabilityHelperUtil.getPlayerCustomSwingAnimProgress((EntityPlayer)entity, partialTicks);
            AnimationsSpear.preformSpearSwingItemRotations3edPerson(entity, hand, partialTicks, fucker, model, true);
        }
        else
        {
            if (entity.getActiveItemStack() == stack) AnimationsSpear.preformSpearUseItemRotations3edPerson(entity, hand, partialTicks, model.swingProgress, model, spearDef);
            else AnimationsSpear.preformSpearSwingItemRotations3edPerson(entity, hand, partialTicks, model.swingProgress, model);
        }

        Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transforms, flag);
        GlStateManager.popMatrix();
    }
}