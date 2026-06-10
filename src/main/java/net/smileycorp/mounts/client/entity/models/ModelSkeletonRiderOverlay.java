package net.smileycorp.mounts.client.entity.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSkeletonRiderOverlay extends ModelBiped
{
    private final ModelRenderer bodyouter;
    private final ModelRenderer headouter;
    private final ModelRenderer left_armouter;
    private final ModelRenderer right_armouter;
    private final ModelRenderer left_legouter;
    private final ModelRenderer right_legouter;

    public ModelSkeletonRiderOverlay()
    {
        super(0, 0, 0,0);
        textureWidth = 64;
        textureHeight = 64;

        bodyouter = new ModelRenderer(this);
        bodyouter.cubeList.add(new ModelBox(bodyouter, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.1F, false));

        headouter = new ModelRenderer(this);
        headouter.cubeList.add(new ModelBox(headouter, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));

        left_armouter = new ModelRenderer(this);
        left_armouter.cubeList.add(new ModelBox(left_armouter, 0, 16, -1.15F, -2.1F, -2.0F, 4, 12, 4, 0.01F, false));
        left_armouter.cubeList.add(new ModelBox(left_armouter, 40, 15, -1.05F, -2.1F, -1.5F, 3, 12, 3, 0.0F, false));

        right_armouter = new ModelRenderer(this);
        right_armouter.cubeList.add(new ModelBox(right_armouter, 52, 15, -1.95F, -2.1F, -1.5F, 3, 12, 3, 0.05F, true));

        left_legouter = new ModelRenderer(this);
        left_legouter.cubeList.add(new ModelBox(left_legouter, 40, 0, -1.5F, 0.0F, -1.6F, 3, 12, 3, 0.1F, true));

        right_legouter = new ModelRenderer(this);
        right_legouter.cubeList.add(new ModelBox(right_legouter, 52, 0, -1.5F, 0.0F, -1.6F, 3, 12, 3, 0.1F, false));

        this.bipedHead.addChild(headouter);
        this.bipedBody.addChild(bodyouter);
        this.bipedLeftArm.addChild(left_armouter);
        this.bipedRightArm.addChild(right_armouter);
        this.bipedLeftLeg.addChild(left_legouter);
        this.bipedRightLeg.addChild(right_legouter);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    { super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale); }
}