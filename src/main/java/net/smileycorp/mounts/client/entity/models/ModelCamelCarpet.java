package net.smileycorp.mounts.client.entity.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCamelCarpet extends ModelCamel
{
    private final ModelRenderer carpet;
    private final ModelRenderer head_bone;
    private final ModelRenderer earR2;
    private final ModelRenderer earL2;
    private final ModelRenderer legFL_bone;
    private final ModelRenderer legFR_bone;
    private final ModelRenderer legBL_bone;
    private final ModelRenderer legBR_bone;
    private final ModelRenderer tail_bone;

    public ModelCamelCarpet()
    {
        /* Remove any render overlap on the main body, this uses its own model. */
        super(0,0);

        textureWidth = 128;
        textureHeight = 128;

        carpet = new ModelRenderer(this);
        carpet.cubeList.add(new ModelBox(carpet, 13, 25, -7.5F, -13.0F, -13.0F, 15, 32, 27, 0.205F, false));
        carpet.cubeList.add(new ModelBox(carpet, 0, 21, -4.5F, -18.0F, -5.0F, 9, 5, 11, 0.205F, false));

        head_bone = new ModelRenderer(this);
        head_bone.cubeList.add(new ModelBox(head_bone, 28, 0, -2.5F, -18.0F, -19.0F, 5, 5, 6, 0.205F, false));
        head_bone.cubeList.add(new ModelBox(head_bone, 0, 0, -3.5F, -18.0F, -13.0F, 7, 14, 7, 0.205F, false));
        head_bone.cubeList.add(new ModelBox(head_bone, 33, 0, -3.5F, -4.0F, -13.0F, 7, 8, 17, 0.205F, false));

        earR2 = new ModelRenderer(this);
        earR2.cubeList.add(new ModelBox(earR2, 24, 0, 0.3284F, -3.2784F, 66.0F, 3, 1, 2, 0.205F, false));

        earL2 = new ModelRenderer(this);
        earL2.cubeList.add(new ModelBox(earL2, 24, 3, 2.3284F, 2.3784F, 66.0F, 3, 1, 2, 0.205F, true));

        legFL_bone = new ModelRenderer(this);
        legFL_bone.cubeList.add(new ModelBox(legFL_bone, 88, 0, 7.4F, -1.0F, -2.5F, 5, 21, 5, 0.205F, false));

        legFR_bone = new ModelRenderer(this);
        legFR_bone.cubeList.add(new ModelBox(legFR_bone, 108, 0, 1.6F, -1.0F, -2.5F, 5, 21, 5, 0.205F, false));

        legBL_bone = new ModelRenderer(this);
        legBL_bone.cubeList.add(new ModelBox(legBL_bone, 88, 26, -2.6F, -1.0F, -2.5F, 5, 21, 5, 0.205F, false));

        legBR_bone = new ModelRenderer(this);
        legBR_bone.cubeList.add(new ModelBox(legBR_bone, 108, 26, -2.4F, -1.0F, -2.5F, 5, 21, 5, 0.205F, true));

        tail_bone = new ModelRenderer(this);
        tail_bone.cubeList.add(new ModelBox(tail_bone, 64, 0, -3.5F, 0.0436F, -0.0009F, 7, 15, 0, 0.205F, false));

        upper_body.addChild(carpet);
        head.addChild(head_bone);
        legFL.addChild(legFL_bone);
        legFR.addChild(legFR_bone);
        legBL.addChild(legBL_bone);
        legBR.addChild(legBR_bone);
        tail.addChild(tail_bone);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    { this.main.render(scale); }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    { super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime); }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}