package net.smileycorp.mounts.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.common.Constants;

@SideOnly(Side.CLIENT)
public class ParticleSpearPierce extends ParticleBase
{
    private static final ResourceLocation PIERCE_TEXTURE = new ResourceLocation(Constants.MODID, "textures/particles/spear_pierce.png");
    Vec3d direction;

    public ParticleSpearPierce(TextureManager textureManager, World world, double x, double y, double z, double movementX, double movementY, double movementZ, int playerYawIn)
    {
        super(textureManager, world, x, y, z, movementX, movementY, movementZ, PIERCE_TEXTURE, 0);
        this.textureManager = textureManager;
        this.motionX = movementX;
        this.motionY = movementY;
        this.motionZ = movementZ;
        this.particleMaxAge = 5;
        this.texSheetSeg = 3;
        this.canCollide = false;
        this.particleScale = 7.0F;

        this.direction = new Vec3d(motionX, motionY, motionZ);
    }

    public void onUpdate()
    {
        super.onUpdate();

        float f = ((float)this.particleAge ) / ((float)this.particleMaxAge / 2);
        this.particleScale += f/2;
        this.texSpot = Math.min(this.particleAge * 6 / (this.particleMaxAge), 5);
    }

    /* StackOverflow has the WEIRDEST stuff like damn. */
    public Vec3d[] particleVertexRendering(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ, float particleSize)
    { return quadRotateToFacing(generateBasicQuad(particleSize, (float)Math.toRadians(75),0,0), this.direction); }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
    {
        @Override
        public Particle createParticle(int particleId, World world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
        {
            switch (parameters.length)
            {
                case 0:
                    return new ParticleSpearPierce(Minecraft.getMinecraft().getTextureManager(), world, posX, posY, posZ, speedX, speedY, speedZ, 0);
                case 1:
                    return new ParticleSpearPierce(Minecraft.getMinecraft().getTextureManager(), world, posX, posY, posZ, speedX, speedY, speedZ, parameters[0]);
            }
            return null;
        }
    }
}