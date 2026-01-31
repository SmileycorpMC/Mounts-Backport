package net.smileycorp.mounts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.atlas.api.util.MathUtils;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.entity.EntityCamel;

@Mod.EventBusSubscriber
public class MountsClientEvents
{
    private static float prevSpaceHeld = 0.0F;
    private static float currSpaceHeld = 0.0F;
    private static final ResourceLocation TEXTURE_NAUTILUS_CHARGE_BAR = new ResourceLocation(Constants.MODID, "textures/gui/camel_charge_bar.png");
    public static boolean swingSpear = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        prevSpaceHeld = currSpaceHeld;

        if (mc.player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null))
        { currSpaceHeld = mc.player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null).getSpaceHeldTime(); }
    }


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

        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        float spaceHeldTime = prevSpaceHeld + (currSpaceHeld - prevSpaceHeld) * partialTicks;

        GlStateManager.enableTexture2D();
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.getTextureManager().bindTexture(TEXTURE_NAUTILUS_CHARGE_BAR);

        mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 0, 182, 5);

        float powerResult = spaceHeldTime / 0.9F;

        if (spaceHeldTime > 0.9F)
        {
            float t = (spaceHeldTime - 0.9F) / 0.32F;
            powerResult -= t;
        }

        int filled = (int)(powerResult * 182);
        if (filled > 0)
        {
            //System.out.print("Power Result: " + powerResult);
            mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 5, filled, 5);
        }

        if (ridden.getDashCooldown() > 0) { mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 10, 182, 5); }
    }


    @SubscribeEvent
    public static void logIn(PlayerEvent.PlayerLoggedInEvent event) {
        swingSpear = false;
    }

    @SubscribeEvent
    public static void renderItem(RenderSpecificHandEvent event) {
        ItemStack stack = event.getItemStack();
        EnumHand hand = event.getHand();
        if (!(stack.getItem() instanceof ItemSpear)) {
            if (swingSpear && hand == EnumHand.MAIN_HAND) swingSpear = false;
            return;
        }
        event.setCanceled(true);
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        ItemSpear spear = (ItemSpear) stack.getItem();
        float partialTicks = event.getPartialTicks();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        GlStateManager.pushMatrix();
        float swing = player.getCooledAttackStrength(partialTicks);
        if (hand == EnumHand.MAIN_HAND && swing < 1 && swingSpear)
        {
            float swingS1End = 0.1f;
            float swingS2End = 0.5f;
            float spearSwing = swing < swingS1End ? swing / swingS1End : swing < swingS2End ? 1.0f : 1.0f - (swing - swingS2End) / swingS2End;

            float spearThrustS1End = 0.2f;
            float spearThrustS2End = 0.3f;
            float spearThrustS3End = 0.5f;
            float spearThrustDistance = 1.5f;
            float spearThrust = swing < spearThrustS1End ? spearThrustDistance - ((swing / spearThrustS1End) / (spearThrustS2End - spearThrustS1End)) * spearThrustS3End :
                    swing < spearThrustS3End ? spearThrustDistance / (swing / spearThrustS1End) :  spearThrustS3End - (swing - spearThrustS3End) / spearThrustS3End;

            GlStateManager.rotate(80f * spearSwing, -1, 0 , 0);
            GlStateManager.translate(0,  spearSwing + spearThrust * 0.25F, 0.2 * spearSwing);
        }
        if (swingSpear && swing == 1) swingSpear = false;
        itemRenderer.renderItemInFirstPerson(player, partialTicks, event.getInterpolatedPitch(), hand, 0, stack, swingSpear ? 0 : event.getEquipProgress());
        GlStateManager.popMatrix();
    }

    private static float clampFloat(float value, float start, float end) {
        return MathHelper.clamp(MathUtils.lerp(value, start, end), 0, 1);
    }

}