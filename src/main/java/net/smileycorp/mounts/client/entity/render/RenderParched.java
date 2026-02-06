package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.common.Constants;

@SideOnly(Side.CLIENT)
public class RenderParched extends RenderSkeleton
{
    private static final ResourceLocation PARCHED_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/parched/parched.png");

    public RenderParched(RenderManager p_i47191_1_)
    {
        super(p_i47191_1_);
        this.addLayer(new LayerParchedOverlay(this));
        this.addLayer(new LayerParchedEmissive(this));
    }

    protected ResourceLocation getEntityTexture(AbstractSkeleton entity)
    { return PARCHED_TEXTURE; }
}