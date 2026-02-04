package net.smileycorp.mounts.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.common.entity.EntityCamel;

@SideOnly(Side.CLIENT)
public class ModelCamel extends ModelBase
{
    public final ModelRenderer main;
    public final ModelRenderer upper_body;
    public final ModelRenderer head;
    private final ModelRenderer earR;
    private final ModelRenderer earL;
    private final ModelRenderer tail;
    private final ModelRenderer legFL;
    private final ModelRenderer legFR;
    private final ModelRenderer legBL;
    private final ModelRenderer legBR;

    public ModelCamel() { this(128, 128); }

    public ModelCamel(int widthIn, int heightIn)
    {
        textureWidth = widthIn;
        textureHeight = heightIn;

        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);


        upper_body = new ModelRenderer(this);
        upper_body.setRotationPoint(0.0F, -19.0F, 0.0F);
        main.addChild(upper_body);
        upper_body.cubeList.add(new ModelBox(upper_body, 13, 25, -7.5F, -13.0F, -13.0F, 15, 12, 27, 0.0F, false));
        upper_body.cubeList.add(new ModelBox(upper_body, 12, 89, -7.5F, -1.0F, -13.0F, 15, 12, 27, 0.0F, false));
        upper_body.cubeList.add(new ModelBox(upper_body, 0, 21, -4.5F, -18.0F, -5.0F, 9, 5, 11, 0.0F, false));


        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -7.0F, -11.0F);
        upper_body.addChild(head);
        head.cubeList.add(new ModelBox(head, 28, 0, -2.5F, -18.0F, -19.0F, 5, 5, 6, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 0, 0, -3.5F, -18.0F, -13.0F, 7, 14, 7, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 33, 0, -3.5F, -4.0F, -13.0F, 7, 8, 17, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 0, 70, -2.5F, -13.0F, -19.0F, 5, 5, 6, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 17, 64, -3.5F, 4.0F, -13.0F, 7, 8, 17, 0.0F, false));

        earR = new ModelRenderer(this);
        earR.setRotationPoint(-3.5F, -17.25F, -7.5F);
        head.addChild(earR);
        setRotationAngle(earR, 0.0F, 0.0F, 0.7854F);
        earR.cubeList.add(new ModelBox(earR, 24, 0, -2.5F, -0.45F, -1.0F, 3, 1, 2, 0.0F, false));

        earL = new ModelRenderer(this);
        earL.setRotationPoint(3.5F, -17.25F, -7.5F);
        head.addChild(earL);
        setRotationAngle(earL, 0.0F, 0.0F, -0.7854F);
        earL.cubeList.add(new ModelBox(earL, 24, 3, -0.5F, -0.45F, -1.0F, 3, 1, 2, 0.0F, true));

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, -10.0F, 14.0F);
        upper_body.addChild(tail);
        setRotationAngle(tail, 0.0436F, 0.0F, 0.0F);
        tail.cubeList.add(new ModelBox(tail, 64, 0, -3.5F, 0.0F, 0.0F, 7, 15, 0, 0.0F, false));

        legFL = new ModelRenderer(this);
        legFL.setRotationPoint(5.0F, -20.0F, -9.5F);
        main.addChild(legFL);
        legFL.cubeList.add(new ModelBox(legFL, 88, 0, -2.6F, -1.0F, -2.5F, 5, 21, 5, 0.0F, false));

        legFR = new ModelRenderer(this);
        legFR.setRotationPoint(-5.0F, -20.0F, -9.5F);
        main.addChild(legFR);
        legFR.cubeList.add(new ModelBox(legFR, 108, 0, -2.4F, -1.0F, -2.5F, 5, 21, 5, 0.0F, false));

        legBL = new ModelRenderer(this);
        legBL.setRotationPoint(5.0F, -20.0F, 10.5F);
        main.addChild(legBL);
        legBL.cubeList.add(new ModelBox(legBL, 88, 26, -2.6F, -1.0F, -2.5F, 5, 21, 5, 0.0F, false));

        legBR = new ModelRenderer(this);
        legBR.setRotationPoint(-5.0F, -20.0F, 10.5F);
        main.addChild(legBR);
        legBR.cubeList.add(new ModelBox(legBR, 108, 26, -2.4F, -1.0F, -2.5F, 5, 21, 5, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        if (this.isChild)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * f5, 0.0F);
            this.main.render(f5);
            GlStateManager.popMatrix();
        }
        else
        {
            main.render(f5);
        }

    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        EntityCamel camel = (EntityCamel) entityIn;
        float animationTime = camel.getClientAnimationTime(ageInTicks - (float)camel.ticksExisted);
        float dashCooldownTime = camel.getClientDashCooldownTime(ageInTicks - (float)camel.ticksExisted);

        /* Used for a 'breathing' motion. */
        float idle = MathHelper.sin((ageInTicks) * 0.05F) * 0.25F;

        this.head.rotateAngleX = headPitch * 0.017453292F + (dashCooldownTime * 0.015F);
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;

        this.earL.rotateAngleY = 0.0F;
        this.earR.rotateAngleY = -this.earL.rotateAngleY;
        this.earL.rotateAngleZ = -0.7854F;
        this.earR.rotateAngleZ = -this.earL.rotateAngleZ;

        this.main.rotateAngleX = 0.0F;
        this.main.rotateAngleZ = 0.0F;

        this.upper_body.offsetY = 0.0F;
        this.upper_body.offsetZ = 0.0F;
        this.upper_body.rotateAngleX = 0.0F;

        this.legFR.offsetX = 0.0F;
        this.legFL.offsetX = this.legFR.offsetX;
        this.legFR.rotateAngleZ = 0.0F;
        this.legFL.rotateAngleZ = 0.0F;
        this.legBR.rotateAngleZ = 0.0F;
        this.legBL.rotateAngleZ = 0.0F;
        this.tail.rotateAngleX = 0.05F;
        this.tail.rotateAngleZ = 0.0F;

        this.legFR.offsetZ = 0.0F;
        this.legFR.offsetY = 0.0F;
        this.legFR.rotateAngleX = 0.0F;
        this.legFR.rotateAngleY = 0.0F;
        this.legFL.offsetZ = this.legFR.offsetZ;
        this.legFL.offsetY = this.legFR.offsetY;
        this.legFL.rotateAngleX = this.legFR.rotateAngleX;
        this.legFL.rotateAngleY = -this.legFR.rotateAngleY;

        this.legBR.offsetX = 0.0F;
        this.legBR.offsetY = this.legFR.offsetY;
        this.legBR.offsetZ = 0.0F;
        this.legBR.rotateAngleX = this.legFR.rotateAngleX;
        this.legBR.rotateAngleY = this.legFR.rotateAngleY;
        this.legBL.offsetX = -this.legBR.offsetX;
        this.legBL.offsetY = this.legFR.offsetY;
        this.legBL.offsetZ = this.legBR.offsetZ;
        this.legBL.rotateAngleX = this.legFR.rotateAngleX;
        this.legBL.rotateAngleY = -this.legFR.rotateAngleY;

        /* A clamped Sin function, First gets the rate it occurs in a Sin, then cuts it so only peaks above the wave are used, finally multiplies it to a usable range.
         *
         * ...Thanks Fresh Animations, lol.
         * */
        float pulse = MathHelper.clamp((MathHelper.sin(camel.ticksExisted * 0.05F) - 0.925F) * 10.0F, 0.0F, 1.0F);
        /* A separate Sin Wave, with one a basic one scaled by the pulse. */
        float earTwitch = MathHelper.sin(camel.ticksExisted * 1.5F) * pulse;

        this.head.rotateAngleX += idle * 0.25F;
        this.earL.rotateAngleZ += idle + (1.4F * pulse) + earTwitch;
        this.earR.rotateAngleZ -= idle + (1.4F * pulse) + earTwitch;

        float pulseTail = MathHelper.clamp((MathHelper.sin(camel.ticksExisted * 0.15F) - 0.1F) * 0.2F, 0.0F, 1.0F);
        float tailTwitch = MathHelper.sin(camel.ticksExisted * 0.1F) * pulseTail;

        this.tail.rotateAngleZ += tailTwitch;

        switch (camel.getAnimState())
        {
            /* Walking is handled here! */
            case NONE:
                if (camel.getDashing() <= 0)
                {
                    /* Used to better sync the motion of the Camel for the Rider and other Clients. Very stupid BAD. */
                    boolean isRiderClient = camel.isBeingRidden()  && camel.getControllingPassenger() == Minecraft.getMinecraft().player;
                    float limbSwingFix = isRiderClient ? 0.75F : 1.0F;

                    /* Speed!! */
                    float walkAnimSpeed = camel.isBeingRidden() ? 0.25F : 0.5F;
                    float animLimbSwing = limbSwing * (camel.isBeingRidden() ? 1.25F : 1.25F) * limbSwingFix;
                    float animLimbSwingAmount = limbSwingAmount * (camel.isBeingRidden() ? 0.5F : 0.7F) * limbSwingFix;


                    float swingPhase = animLimbSwing * walkAnimSpeed;
                    float legSwingFR = MathHelper.cos(swingPhase) * 2F * animLimbSwingAmount;
                    float legSwingFL = MathHelper.cos(swingPhase + (float)Math.PI) * 2F * animLimbSwingAmount;
                    float legSwingBR = MathHelper.cos(swingPhase + 0.3F) * 2F * animLimbSwingAmount;
                    float legSwingBL = MathHelper.cos(swingPhase + 0.3F + (float)Math.PI) * 2F * animLimbSwingAmount;

                    this.earL.rotateAngleZ += Math.abs(legSwingFR * 1.5F);
                    this.earR.rotateAngleZ -= Math.abs(legSwingFR * 1.5F);

                    this.head.rotateAngleX += Math.abs(legSwingFR * 0.2F);
                    this.head.rotateAngleY += legSwingFR * 0.05F;

                    this.main.rotateAngleZ = legSwingFR * 0.05F;
                    this.tail.rotateAngleZ = legSwingFR;
                    this.tail.rotateAngleX += Math.abs(legSwingFR * 0.5F);

                    this.legFR.rotateAngleX = (legSwingFR < 0) ? legSwingFR : legSwingFR * 0.75F;
                    this.legFL.rotateAngleX = (legSwingFL < 0) ? legSwingFL : legSwingFL * 0.75F;
                    this.legBR.rotateAngleX = (legSwingBR < 0) ? legSwingBR : legSwingBR * 0.75F;
                    this.legBL.rotateAngleX = (legSwingBL < 0) ? legSwingBL : legSwingBL * 0.75F;


                    float liftFR = MathHelper.clamp(MathHelper.sin(swingPhase), 0.0F, 1.0F) * animLimbSwingAmount;
                    float liftFL = MathHelper.clamp(MathHelper.sin(swingPhase + (float)Math.PI), 0.0F, 1.0F) * animLimbSwingAmount;
                    float liftBR = MathHelper.clamp(MathHelper.sin(swingPhase + 0.3F), 0.0F, 1.0F) * animLimbSwingAmount;
                    float liftBL = MathHelper.clamp(MathHelper.sin(swingPhase + 0.3F + (float)Math.PI), 0.0F, 1.0F) * animLimbSwingAmount;
                    float liftStrength = 0.5F;

                    this.legFR.offsetY = liftFR * -liftStrength;
                    this.legFL.offsetY = liftFL * -liftStrength;
                    this.legBR.offsetY = liftBR * -liftStrength;
                    this.legBL.offsetY = liftBL * -liftStrength;

                    float legZ = MathHelper.sin(swingPhase) * 0.15F * Math.min(animLimbSwingAmount * 3, 0.5F);
                    this.legFR.offsetZ =  legZ;
                    this.legFL.offsetZ = -legZ;
                    this.legBR.offsetZ =  legZ;
                    this.legBL.offsetZ = -legZ;
                }
                else
                {
                    float headSwing = MathHelper.cos(limbSwing * 0.5F) * limbSwingAmount;
                    float legSwing = MathHelper.cos(limbSwing * 0.5F) * (limbSwingAmount * 2);

                    //this.main.rotateAngleX = 0.1F - (dashCooldownTime * 0.1F);

                    this.earL.rotateAngleY = -1.0F;
                    this.earR.rotateAngleY = -this.earL.rotateAngleY;
                    this.earL.rotateAngleZ = 0.0F;
                    this.earR.rotateAngleZ = this.earL.rotateAngleZ;

                    //this.head.rotateAngleX = 0.1F + headSwing * 0.1F;

                    this.legFR.rotateAngleX = legSwing;
                    this.legFL.rotateAngleX = -this.legFR.rotateAngleX;
                    this.legBR.rotateAngleX = -this.legFR.rotateAngleX;
                    this.legBL.rotateAngleX = this.legFR.rotateAngleX;

                    this.tail.rotateAngleX = 1.5F + (headSwing * 0.5F);
                }

                break;
            case SIT_START:
                /* Segments intentionally overlap to create more naturalistic blending. */
                float key1 = segmentAnimationTime(animationTime, 0.0F, 0.6F);
                float key2 = segmentAnimationTime(animationTime, 0.05F, 1.0F);
                float key3 = segmentAnimationTime(animationTime, 0.85F, 1.0F);

                this.legFR.offsetX = -0.07F * key2 + (0.07F * key3);
                this.legFL.offsetX = -this.legFR.offsetX;
                this.legFR.offsetY = 0.2F * key1 + (0.2F * key2) + (0.7F * key3);
                this.legFL.offsetY = this.legFR.offsetY;
                this.legFR.offsetZ = 0.5F * key1;
                this.legFL.offsetZ = this.legFR.offsetZ;
                this.legFR.rotateAngleX = -0.3F * key1 + (-0.6F * key2) + (-0.66F * key3);
                this.legFL.rotateAngleX = this.legFR.rotateAngleX;
                this.legFR.rotateAngleY = 0.1F * key2 + (0.1F * key3);
                this.legFL.rotateAngleY = -this.legFR.rotateAngleY;

                this.legBR.offsetX = -0.07F * key2 + (-0.07F * key3);
                this.legBR.offsetY = this.legFR.offsetY * key3;
                this.legBR.offsetZ = 0.14F * key2;
                this.legBR.rotateAngleX = -0.1F * key1 + (-0.25F * key2) + (-1.22F * key3);
                this.legBR.rotateAngleY = this.legFR.rotateAngleY;
                this.legBL.offsetX = -this.legBR.offsetX;
                this.legBL.offsetZ = this.legBR.offsetZ * key3;
                this.legBL.offsetY = this.legFR.offsetY * key3;
                this.legBL.rotateAngleX = this.legBR.rotateAngleX;
                this.legBL.rotateAngleY = -this.legFR.rotateAngleY * key3;

                this.head.rotateAngleX = (-0.2F * key1) + (-0.2F * key2) + (0.4F * key3);

                this.upper_body.offsetY = 0.2F * key1 + (0.4F * key2) + (0.64F * key3);
                this.upper_body.offsetZ = (0.2F * key2) + (-0.2F * key3);
                this.upper_body.rotateAngleX = 0.6F * key1 + (-0.2F * key2) + (-0.4F * key3);

                this.tail.rotateAngleX = 0.05F + (0.8F * key3);
                break;
            /* This the static sitting pose. */
            case SIT:
                this.upper_body.offsetY = 1.24F;

                this.legFR.offsetY = 1.1F;
                this.legFL.offsetY = this.legFR.offsetY;
                this.legFR.offsetZ = 0.5F;
                this.legFL.offsetZ = this.legFR.offsetZ;
                this.legFR.rotateAngleX = -1.56F;
                this.legFL.rotateAngleX = this.legFR.rotateAngleX;
                this.legFR.rotateAngleY = 0.2F;
                this.legFL.rotateAngleY = -this.legFR.rotateAngleY;

                this.legBR.offsetX = -0.14F;
                this.legBR.offsetY = this.legFR.offsetY;
                this.legBR.offsetZ = 0.14F;
                this.legBR.rotateAngleX = -1.57F;
                this.legBR.rotateAngleY = this.legFR.rotateAngleY;
                this.legBL.offsetX = -this.legBR.offsetX;
                this.legBL.offsetZ = this.legBR.offsetZ;
                this.legBL.offsetY = this.legFR.offsetY;
                this.legBL.rotateAngleX = this.legBR.rotateAngleX;
                this.legBL.rotateAngleY = -this.legFR.rotateAngleY;

                this.tail.rotateAngleX = 0.85F;
                break;
            case SIT_END:
                /* Segments intentionally overlap to create more naturalistic blending. */
                float keyA1 = segmentAnimationTime(animationTime, 0.0F, 0.4F);
                float keyA2 = segmentAnimationTime(animationTime, 0.2F, 0.8F);
                float keyA3 = segmentAnimationTime(animationTime, 0.7F, 1.0F);

                this.upper_body.offsetY = 1.24F + (-0.24F * keyA1) + (-0.8F * keyA2) + (-0.2F * keyA3);
                this.upper_body.rotateAngleX = 0.0F + (-0.25F * keyA1) + (0.1F * keyA2) + (0.15F * keyA3);

                this.head.rotateAngleX = 0.0F + (0.8F * keyA1) + (-0.8F * keyA3);

                this.legFR.offsetY = 1.1F + (-0.5F * keyA1) + (-0.5F * keyA2) + (-0.1F * keyA3);
                this.legFL.offsetY = this.legFR.offsetY;
                this.legFR.offsetZ = 0.5F + (-0.5F * keyA2);
                this.legFL.offsetZ = this.legFR.offsetZ;
                this.legFR.rotateAngleX = -1.56F + (0.5F * keyA1) + (0.66F * keyA2) + (0.4F * keyA3);
                this.legFL.rotateAngleX = this.legFR.rotateAngleX;
                this.legFR.rotateAngleY = 0.2F + (-0.2F * keyA3);
                this.legFL.rotateAngleY = -this.legFR.rotateAngleY;

                this.legBR.offsetX = -0.14F + (0.24F * keyA1) + (-0.1F * keyA3);
                this.legBR.offsetY = 1.1F + (-0.95F * keyA2) + (-0.15F * keyA3);
                this.legBR.offsetZ = 0.14F + (-0.24F * keyA1) + (0.1F * keyA3);
                this.legBR.rotateAngleX = -1.57F + (0.1F * keyA1) + (2.0F * keyA2) + (-0.53F * keyA3);
                this.legBR.rotateAngleY = 0.2F + (-0.2F * keyA1);
                this.legBL.offsetX = -this.legBR.offsetX;
                this.legBL.offsetZ = this.legBR.offsetZ;
                this.legBL.offsetY = this.legBR.offsetY;
                this.legBL.rotateAngleX = this.legBR.rotateAngleX;
                this.legBL.rotateAngleY = -this.legBR.rotateAngleY;

                this.tail.rotateAngleX = 0.85F + (0.15F * keyA1) + (-0.55F * keyA2) + (-0.4F * keyA3);
                break;
        }
    }

    /** Created a segment (0F -> 1.0F float) within the specified time of the animation. Like pseudo keyframes? */
    private float segmentAnimationTime(float animTimeIn, float segStart, float segEnd)
    { return MathHelper.clamp((animTimeIn - segStart) / (segEnd - segStart), 0.0F, 1.0F); }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}