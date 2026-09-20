package net.smileycorp.mounts.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.common.Constants;

@SideOnly(Side.CLIENT)
public class ParticleZap extends ParticleBase
{
    final float rotSpeed;
    private static final ResourceLocation ZAP_TEXTURE = new ResourceLocation(Constants.MODID, "textures/particles/zap.png");

    public ParticleZap(TextureManager textureManager, World world, double x, double y, double z, double movementX, double movementY, double movementZ, int playerYawIn)
    {
        super(textureManager, world, x, y, z, movementX, movementY, movementZ, ZAP_TEXTURE, 0);
        this.textureManager = textureManager;
        this.motionX = movementX;
        this.motionY = movementY;
        this.motionZ = movementZ;
        this.particleMaxAge = 5;
        this.texSheetSeg = 3;
        this.canCollide = false;
        this.particleScale = 2.0F;

        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
    }

    public void onUpdate()
    {
        super.onUpdate();

        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += (float)Math.PI * this.rotSpeed * 2.0F;

        this.motionX *= 0.99;
        this.motionZ *= 0.99;

        this.texSpot = Math.min(this.particleAge * 9 / (this.particleMaxAge), 8);
        if (this.particleAge > this.particleMaxAge / 2) this.setAlphaF(1.2F - (float)particleAge / (float)this.particleMaxAge - ((float)(this.particleMaxAge / 2)));
    }

    /* StackOverflow has the WEIRDEST stuff like damn. */
    /* StackOverflow has the WEIRDEST stuff like damn. */
    public Vec3d[] particleVertexRendering(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ, float particleSize)
    {
        Vec3d[] wow = new Vec3d[]{
                new Vec3d(-rotationX * particleSize - rotationXY * particleSize, -rotationZ * particleSize, -rotationYZ * particleSize - rotationXZ * particleSize),
                new Vec3d(-rotationX * particleSize + rotationXY * particleSize, rotationZ * particleSize, -rotationYZ * particleSize + rotationXZ * particleSize),
                new Vec3d(rotationX * particleSize + rotationXY * particleSize, rotationZ * particleSize, rotationYZ * particleSize + rotationXZ * particleSize),
                new Vec3d(rotationX * particleSize - rotationXY * particleSize, -rotationZ * particleSize, rotationYZ * particleSize - rotationXZ * particleSize)
        };

        float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
        float f9 = MathHelper.cos(angle * 0.5F);
        float f10 = MathHelper.sin(angle * 0.5F) * (float)cameraViewDir.x;
        float f11 = MathHelper.sin(angle * 0.5F) * (float)cameraViewDir.y;
        float f12 = MathHelper.sin(angle * 0.5F) * (float)cameraViewDir.z;
        Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

        for (int l = 0; l < 4; ++l)
        {
            wow[l] = vec3d.scale(2.0D * wow[l].dotProduct(vec3d)).add(wow[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(wow[l]).scale((double)(2.0F * f9)));
        }

        return wow;
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
    {
        @Override
        public Particle createParticle(int particleId, World world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
        {
            switch (parameters.length)
            {
                case 0:
                    return new ParticleZap(Minecraft.getMinecraft().getTextureManager(), world, posX, posY, posZ, speedX, speedY, speedZ, 0);
                case 1:
                    return new ParticleZap(Minecraft.getMinecraft().getTextureManager(), world, posX, posY, posZ, speedX, speedY, speedZ, parameters[0]);
            }
            return null;
        }
    }
}