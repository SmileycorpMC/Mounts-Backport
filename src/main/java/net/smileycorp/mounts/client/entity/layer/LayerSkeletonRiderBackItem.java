package net.smileycorp.mounts.client.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.common.entity.EntitySkeletonRider;

public class LayerSkeletonRiderBackItem implements LayerRenderer<EntitySkeletonRider>
{
    private final RenderLivingBase<?> skeletonRiderRenderer;

    public LayerSkeletonRiderBackItem(RenderLivingBase<?> livingEntityRendererIn)
    {
        this.skeletonRiderRenderer = livingEntityRendererIn;
    }

    public void doRenderLayer(EntitySkeletonRider entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack = entitylivingbaseIn.getBackItem();

        if (!itemstack.isEmpty())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();

            ModelBiped model = (ModelBiped) this.skeletonRiderRenderer.getMainModel();
            ModelRenderer body = model.bipedBody;

            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            GlStateManager.translate(0, 0.35F, 0.16F);
            body.postRender(scale);

            if (item.isFull3D())
            {
                if (item.shouldRotateAroundWhenRendering())
                {
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.translate(0.0F, -0.0625F, 0.0F);
                }
                float scaling3dItem = 0.8F;
                GlStateManager.scale(scaling3dItem, scaling3dItem, scaling3dItem);
                GlStateManager.translate(0, -0.2F, 0);
                this.skeletonRiderRenderer.transformHeldFull3DItemLayer();
            }
            else
            { GlStateManager.scale(0.875F, 0.875F, 0.875F); }

            if (item instanceof ItemSpear)
            {
                float scaling3dItem = 1.8F;
                GlStateManager.scale(scaling3dItem, scaling3dItem, 1);
            }

            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.FIXED);

            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() { return false; }
}