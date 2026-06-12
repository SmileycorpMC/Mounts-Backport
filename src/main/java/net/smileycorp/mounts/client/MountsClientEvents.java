package net.smileycorp.mounts.client;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.client.animation.AnimationsSpear;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearAnimation;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.entity.EntityCamel;
import net.smileycorp.mounts.common.network.HoldingSpaceMessage;
import net.smileycorp.mounts.common.network.PacketHandler;
import net.smileycorp.mounts.common.network.SpearAnimSwingServerMessage;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(Side.CLIENT)
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

    /** This informs the server that the Player has pressed Space. */
    @SubscribeEvent
    public static void onUpdateJump(InputUpdateEvent event)
    {
        boolean isJumping = event.getMovementInput().jump;

        EntityPlayer player = event.getEntityPlayer();

        if (player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null))
        {
            CapabilitySpearMovement.ICapabilityMountsPlayerInfo capCharge = player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null);

            if (isJumping != capCharge.getIsSpaceHeld())
            {
                PacketHandler.NETWORK_INSTANCE.sendToServer(new HoldingSpaceMessage(event.getEntityPlayer().getEntityId(), isJumping));
                capCharge.setIsSpaceHeld(isJumping);
            }
        }
    }

    /** This sends a packet of the custom swing's animation speed. */
    @SubscribeEvent
    public static void onMouseClick(MouseEvent event)
    {
        if (event.getButton() != 0 || !event.isButtonstate()) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        ItemStack stack = player.getHeldItemMainhand();

        if (!(stack.getItem() instanceof ItemSpear)) return;

        double attackSpeed = 4.0D;
        Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

        if (modifiers.containsKey(SharedMonsterAttributes.ATTACK_SPEED.getName()))
        {
            for (AttributeModifier mod : modifiers.get(SharedMonsterAttributes.ATTACK_SPEED.getName()))
            { attackSpeed += mod.getAmount(); }
        }
        int duration = (int)(30.0D / attackSpeed);


        CapabilitySpearAnimation.ICapabilityAnimations anim = player.getCapability(CapabilitySpearAnimation.MOUNTS_PLAYER_ANIM_CAP, null);
        if (anim == null) return;

        anim.setCustomSwingStartTime(player.ticksExisted);
        anim.setCustomSwingEndTime(player.ticksExisted + duration);

        PacketHandler.NETWORK_INSTANCE.sendToServer(new SpearAnimSwingServerMessage(player.getEntityId(), duration));
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
    public static void renderItem(RenderSpecificHandEvent event)
    {
        /* TEMP EARLY EXIT FOR TESTING */
        if (true) return;

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



    /** Remember, this hook leads to `ItemRenderer`, so check it for transformation info and bullshit. */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderFirstPersonTrident(@Nonnull RenderSpecificHandEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ItemRenderer renderer = Minecraft.getMinecraft().getItemRenderer();
        float partialTicks = event.getPartialTicks();
        final EnumHandSide arm = event.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();

        boolean isRightArm = arm == EnumHandSide.RIGHT;
        float cooldownStrength = player.getCooledAttackStrength(partialTicks);

        if(stack.getItem() instanceof ItemSpear)
        {
            SpearDefinition spearDef = ((ItemSpear) stack.getItem()).getDefinition();

            if (player.isHandActive() && player.getActiveHand() == event.getHand())
            {
                GlStateManager.pushMatrix();
                AnimationsSpear.preformSpearUseItemRotations1stPerson(player, cooldownStrength, partialTicks, arm, spearDef);;
                renderer.renderItemInFirstPerson(Minecraft.getMinecraft().player, partialTicks, event.getInterpolatedPitch(), event.getHand(), 0, stack, 0);
                GlStateManager.popMatrix();
                event.setCanceled(true);
            }
            else if (cooldownStrength > 0 && swingSpear)
            {
                GlStateManager.pushMatrix();
                AnimationsSpear.preformSpearSwingItemRotations1stPerson(player, cooldownStrength, partialTicks, arm);
                renderer.renderItemInFirstPerson(Minecraft.getMinecraft().player, partialTicks, event.getInterpolatedPitch(), event.getHand(), 0, stack, 0);
                GlStateManager.popMatrix();
                event.setCanceled(true);
            }
        }
        else if (event.getHand() == EnumHand.MAIN_HAND) swingSpear = false;
    }
}