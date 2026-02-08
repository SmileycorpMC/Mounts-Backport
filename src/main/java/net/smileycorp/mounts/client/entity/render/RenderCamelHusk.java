package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityCamelHusk;

@SideOnly(Side.CLIENT)
public class RenderCamelHusk extends RenderCamel<EntityCamelHusk>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/camel/camel_husk.png");

    public RenderCamelHusk(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.addLayer(new LayerCamelChuffingIt(this));
        this.addLayer(new LayerCamelSaddle(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCamelHusk entity) { return TEXTURE; }
}