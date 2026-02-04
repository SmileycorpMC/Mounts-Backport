package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.client.entity.ModelCamel;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityCamel;

@SideOnly(Side.CLIENT)
public class RenderCamel extends RenderLiving<EntityCamel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/camel/camel.png");

    public RenderCamel(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelCamel(), 1.1f);
        this.addLayer(new LayerCamelChuffingIt(this));
        this.addLayer(new LayerCamelSaddle(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCamel entity)
    { return TEXTURE; }
}