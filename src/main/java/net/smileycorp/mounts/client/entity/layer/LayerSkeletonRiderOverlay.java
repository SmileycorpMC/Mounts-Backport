package net.smileycorp.mounts.client.entity.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.client.entity.models.ModelSkeletonRiderOverlay;
import net.smileycorp.mounts.client.entity.render.RenderSkeletonRider;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntitySkeletonRider;

@SideOnly(Side.CLIENT)
public class LayerSkeletonRiderOverlay implements LayerRenderer<EntitySkeletonRider>
{
    private final RenderSkeletonRider riderRender;
    private final ModelSkeletonRiderOverlay overlayModel = new ModelSkeletonRiderOverlay();
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/rider/rider_overlay.png");

    public LayerSkeletonRiderOverlay(RenderSkeletonRider rendererIn)
    { this.riderRender = rendererIn; }

    public void doRenderLayer(EntitySkeletonRider entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!entityIn.isInvisible())
        {
            this.riderRender.bindTexture(OVERLAY_TEXTURE);
            this.overlayModel.setModelAttributes(this.riderRender.getMainModel());
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.overlayModel.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
            this.overlayModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
            this.overlayModel.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    { return true; }
}
