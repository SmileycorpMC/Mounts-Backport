package net.smileycorp.mounts.client.entity;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCamelDart extends ModelCamel
{
    private final ModelRenderer head_bone;
    private final ModelRenderer dart;

    public ModelCamelDart()
    {
        /* Remove any render overlap on the main body, this uses its own model. */
        super(0,0);
        textureWidth = 16;
        textureHeight = 16;

        head_bone = new ModelRenderer(this);

        dart = new ModelRenderer(this);
        dart.setRotationPoint(-2.0F, -14.5F, -18.5F);
        head_bone.addChild(dart);
        setRotationAngle(dart, 0.2618F, 0.8727F, 0.0F);
        dart.cubeList.add(new ModelBox(dart, 0, 0, -0.5F, -0.5F, -4.0F, 1, 1, 4, 0.0F, false));

        head.addChild(head_bone);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    { this.main.render(scale); }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}