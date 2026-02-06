package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.client.entity.ModelParchedOverlay;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityParched;

@SideOnly(Side.CLIENT)
public class LayerParchedOverlay implements LayerRenderer<EntityParched>
{
    private final RenderParched parchedRender;
    private final ModelParchedOverlay overlayModel = new ModelParchedOverlay();
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/parched/parched_overlay.png");

    public LayerParchedOverlay(RenderParched rendererIn)
    { this.parchedRender = rendererIn; }

    public void doRenderLayer(EntityParched entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!entityIn.isInvisible())
        {
            this.parchedRender.bindTexture(OVERLAY_TEXTURE);
            this.overlayModel.setModelAttributes(this.parchedRender.getMainModel());
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
