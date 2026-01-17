package net.smileycorp.mounts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.entity.EntityCamel;

@Mod.EventBusSubscriber
public class MountsClientEvents
{
    private static final ResourceLocation TEXTURE_NAUTILUS_CHARGE_BAR = new ResourceLocation(Constants.MODID, "textures/gui/camel_charge_bar.png");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE)
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.player != null && mc.player.isRiding())
            {
                if (mc.player.getRidingEntity() instanceof EntityCamel)
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderOverlayPost(RenderGameOverlayEvent.Post event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            if (mc.player != null && mc.player.isRiding() && mc.player.getRidingEntity() instanceof EntityCamel)
            { renderChargeBar(mc, (EntityCamel)mc.player.getRidingEntity()); }
        }
    }

    private static void renderChargeBar(Minecraft mc, EntityCamel ridden)
    {
        ScaledResolution res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        int barX = width / 2 - 91;
        int barY = height - 29;

        float progress = 0;
        if (mc.player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null))
        { progress = mc.player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null).getSpaceHeldTime(); }

        GlStateManager.enableTexture2D();
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.getTextureManager().bindTexture(TEXTURE_NAUTILUS_CHARGE_BAR);

        mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 0, 182, 5);

        int filled = (int)(progress * 182);
        if (filled > 0)
        {
            mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 5, filled, 5);
        }

        if (ridden.dashCooldown > 0) { mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 10, 182, 5); }
    }
}