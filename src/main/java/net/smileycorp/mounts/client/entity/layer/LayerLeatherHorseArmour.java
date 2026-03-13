package net.smileycorp.mounts.client.entity.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.IWearsHorseArmor;
import net.smileycorp.mounts.common.items.ItemLeatherHorseArmour;
import net.smileycorp.mounts.common.items.MountsItems;

public class LayerLeatherHorseArmour<T extends EntityLiving> implements LayerRenderer<T> {

    public static final ResourceLocation LAYER_0 = Constants.loc("textures/entities/horse/armor/horse_armor_leather_0.png");
    public static final ResourceLocation LAYER_1 = Constants.loc("textures/entities/horse/armor/horse_armor_leather_1.png");

    private final RenderLiving<T> renderer;
    private final ModelBase model;

    public LayerLeatherHorseArmour(RenderLiving<T> renderer, ModelBase model) {
        this.renderer = renderer;
        this.model = model;
    }

    @Override
    public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!(entity instanceof IWearsHorseArmor)) return;
        IWearsHorseArmor horse = (IWearsHorseArmor) entity;
        ItemStack armour = horse.getHorseArmour();
        if (armour.getItem() != MountsItems.LEATHER_HORSE_ARMOUR) return;
        render(entity, ItemLeatherHorseArmour.getColour(armour), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }

    public void render(T entity, int colour, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        model.setModelAttributes(renderer.getMainModel());
        model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        renderer.bindTexture(LAYER_0);
        GlStateManager.color((float)(colour >> 16 & 255) / 255f, (float)(colour >> 8 & 255) / 255f, (float)(colour & 255) / 255f);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        renderer.bindTexture(LAYER_1);
        GlStateManager.color(1, 1, 1);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

}
