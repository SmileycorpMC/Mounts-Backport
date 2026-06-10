package net.smileycorp.mounts.client.entity.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.client.entity.layer.LayerSkeletonRiderBackItem;
import net.smileycorp.mounts.client.entity.layer.LayerSkeletonRiderEmissive;
import net.smileycorp.mounts.client.entity.layer.LayerSkeletonRiderOverlay;
import net.smileycorp.mounts.common.Constants;

@SideOnly(Side.CLIENT)
public class RenderSkeletonHorseman extends RenderSkeleton
{
    private static final ResourceLocation RIDER_TEXTURE = new ResourceLocation(Constants.MODID, "textures/entities/rider/rider.png");

    public RenderSkeletonHorseman(RenderManager rm)
    {
        super(rm);
        this.addLayer(new LayerSkeletonRiderOverlay(this));
        this.addLayer(new LayerSkeletonRiderBackItem(this));
        this.addLayer(new LayerSkeletonRiderEmissive(this));
    }

    protected ResourceLocation getEntityTexture(AbstractSkeleton entity)
    { return RIDER_TEXTURE; }
}
