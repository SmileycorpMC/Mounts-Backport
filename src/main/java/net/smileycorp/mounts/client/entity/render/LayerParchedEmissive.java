package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityParched;

@SideOnly(Side.CLIENT)
public class LayerParchedEmissive implements LayerRenderer<EntityParched>
{
    private final RenderParched parchedRender;
    private final ModelBiped emissiveModel = new ModelSkeleton(0.025F, false);
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/parched/parched_e.png");

    public LayerParchedEmissive(RenderParched rendererIn)
    { this.parchedRender = rendererIn; }

    public void doRenderLayer(EntityParched entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!entityIn.isInvisible())
        {
            this.parchedRender.bindTexture(OVERLAY_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            this.emissiveModel.setModelAttributes(this.parchedRender.getMainModel());
            this.emissiveModel.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
            this.emissiveModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
            this.emissiveModel.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            this.parchedRender.setLightmap(entityIn);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    { return true; }
}
