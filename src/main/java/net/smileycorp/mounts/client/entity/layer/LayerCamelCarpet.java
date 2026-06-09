package net.smileycorp.mounts.client.entity.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.client.entity.models.ModelCamelCarpet;
import net.smileycorp.mounts.client.entity.render.RenderCamel;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityCamel;

@SideOnly(Side.CLIENT)
public class LayerCamelCarpet implements LayerRenderer<EntityCamel>
{
    private final RenderCamel camelRenderer;
    private final ModelCamelCarpet carpetModel = new ModelCamelCarpet();
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/camel/carpet/camel_carpet_black.png");

    public LayerCamelCarpet(RenderCamel camelRendererIn)
    { this.camelRenderer = camelRendererIn; }

    public void doRenderLayer(EntityCamel entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack decor = entity.getDecorItemStack();
        if (decor.isEmpty()) return;

        ResourceLocation texture = SADDLE_TEXTURE;

        if (decor.getItem() == Item.getItemFromBlock(Blocks.CARPET))
        {
            EnumDyeColor color = EnumDyeColor.byMetadata(decor.getMetadata());

            texture = new ResourceLocation(Constants.MODID, "textures/entities/camel/carpet/camel_carpet_" + color.getName() + ".png");
        }


        this.camelRenderer.bindTexture(texture);

        this.carpetModel.setModelAttributes(this.camelRenderer.getMainModel());
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.carpetModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        this.carpetModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.carpetModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() { return false; }
}