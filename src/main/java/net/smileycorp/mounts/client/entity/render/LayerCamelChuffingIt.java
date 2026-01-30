package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.client.entity.ModelCamelDart;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityCamel;

/**
* And by 'it', I mean that fat dart.
* */
@SideOnly(Side.CLIENT)
public class LayerCamelChuffingIt implements LayerRenderer<EntityCamel>
{
    private final RenderCamel camelRenderer;
    private final ModelCamelDart fatDartModel = new ModelCamelDart();
    private static final ResourceLocation FAT_DART_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/camel/dart.png");

    public LayerCamelChuffingIt(RenderCamel camelRendererIn)
    { this.camelRenderer = camelRendererIn; }

    public void doRenderLayer(EntityCamel entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.camelRenderer.bindTexture(FAT_DART_TEXTURE);
        this.fatDartModel.setModelAttributes(this.camelRenderer.getMainModel());
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.fatDartModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        this.fatDartModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.fatDartModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() { return false; }
}