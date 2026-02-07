package net.smileycorp.mounts.client.entity;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCamelSaddle extends ModelCamel
{
    private final ModelRenderer saddle;
    private final ModelRenderer head_bone;
    private final ModelRenderer band;
    private final ModelRenderer bits;
    private final ModelRenderer reins;

    public ModelCamelSaddle()
    {
        /* Remove any render overlap on the main body, this uses its own model. */
        super(0,0);

        textureWidth = 128;
        textureHeight = 64;

        saddle = new ModelRenderer(this);
        saddle.cubeList.add(new ModelBox(saddle, 13, 25, -7.5F, -13.0F, -13.0F, 15, 12, 27, 0.2F, false));
        saddle.cubeList.add(new ModelBox(saddle, 0, 21, -4.5F, -18.0F, -5.0F, 9, 5, 11, 0.2F, false));
        saddle.cubeList.add(new ModelBox(saddle, 0, 37, -3.5F, -21.0F, -5.0F, 7, 3, 11, 0.2F, false));

        head_bone = new ModelRenderer(this);

        band = new ModelRenderer(this);
        band.setRotationPoint(0.0F, 26.0F, 11.0F);
        head_bone.addChild(band);
        band.cubeList.add(new ModelBox(band, 28, 0, -2.5F, -44.0F, -30.0F, 5, 5, 6, 0.2F, false));
        band.cubeList.add(new ModelBox(band, 0, 0, -3.5F, -44.0F, -24.0F, 7, 14, 7, 0.2F, false));
        band.cubeList.add(new ModelBox(band, 33, 0, -3.5F, -30.0F, -24.0F, 7, 8, 17, 0.2F, false));

        bits = new ModelRenderer(this);
        bits.setRotationPoint(0.0F, 26.0F, 11.0F);
        head_bone.addChild(bits);
        bits.cubeList.add(new ModelBox(bits, 28, 37, 2.5F, -42.0F, -27.0F, 1, 2, 2, 0.1F, false));
        bits.cubeList.add(new ModelBox(bits, 34, 37, -3.5F, -42.0F, -27.0F, 1, 2, 2, 0.1F, false));

        reins = new ModelRenderer(this);
        reins.setRotationPoint(0.0F, -14.5F, -14.5F);
        head_bone.addChild(reins);
        reins.cubeList.add(new ModelBox(reins, 84, 0, -3.5F, -0.25F, -0.25F, 7, 7, 15, 0.21F, false));

        upper_body.addChild(saddle);
        head.addChild(head_bone);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    { this.main.render(scale); }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
        this.reins.showModel = entitylivingbaseIn.isBeingRidden();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}