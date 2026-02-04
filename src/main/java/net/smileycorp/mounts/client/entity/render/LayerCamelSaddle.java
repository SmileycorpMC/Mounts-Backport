package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.client.entity.ModelCamelSaddle;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityCamel;

@SideOnly(Side.CLIENT)
public class LayerCamelSaddle implements LayerRenderer<EntityCamel>
{
    private final RenderCamel camelRenderer;
    private final ModelCamelSaddle saddleModel = new ModelCamelSaddle();
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/camel/camel_saddle.png");

    public LayerCamelSaddle(RenderCamel camelRendererIn)
    { this.camelRenderer = camelRendererIn; }

    public void doRenderLayer(EntityCamel entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (entity.getSaddle().isEmpty()) return;

        this.camelRenderer.bindTexture(SADDLE_TEXTURE);
        this.saddleModel.setModelAttributes(this.camelRenderer.getMainModel());
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.saddleModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        this.saddleModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.saddleModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() { return false; }
}