package net.smileycorp.mounts.api;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;

import java.util.UUID;

public class ItemSpear extends Item {

    protected static final UUID ATTACK_RANGE_MODIFIER = UUID.fromString("b7c01135-0543-4b9f-be02-0c067dc3eca0");

    private final SpearDefinition definition;

    public ItemSpear(SpearDefinition definition) {
        this.definition = definition;
        setMaxStackSize(1);
        String name = definition.getName() + "_spear";
        setRegistryName(Constants.loc(name));
        setUnlocalizedName(Constants.name(name));
        setMaxDamage(definition.getDurability());
        setCreativeTab(definition.getCreativeTab());
        addPropertyOverride(new ResourceLocation("held"), (stack, world, entity) -> entity != null && world != null ? 1 : 0);
    }

    public SpearDefinition getDefinition() {
        return definition;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return super.getItemUseAction(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return definition.getChargeDamageDuration();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
         performSpearAttack(player, true);
        super.onUsingTick(stack, player, count);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        Ingredient repairMaterial = definition.getRepairMaterial();
        return repairMaterial != Ingredient.EMPTY && repairMaterial.apply(repair);
    }

    @Override
    public int getItemEnchantability() {
        return definition.getEnchantability();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);
        if (slot != EntityEquipmentSlot.MAINHAND) return map;
        map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", definition.getDamage() - 1, 0));
        map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", definition.getAttackSpeed() - 4, 0));
        return map;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnumEnchantmentType.WEAPON || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.MAINHAND;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) { return true; }

    public static boolean performSpearAttack(EntityLivingBase user, boolean charge) {
        ItemStack stack = user.getHeldItemMainhand();
        if (user.world.isRemote |! (stack.getItem() instanceof ItemSpear)) return false;
        SpearDefinition definition = ((ItemSpear) stack.getItem()).getDefinition();
        Vec3d look = user.getLookVec();
        BlockPos getUserEyes = user.getPosition().add(new BlockPos(0, user.getEyeHeight(), 0));
        /* Controls the distance the attack box is shifted away from the user. */
        double distanceFromUser = user instanceof EntityPlayer ? 4 : 2;
        double width = 0.25D;

        /* Make a sized Bounding Box, and push it forward by `distanceFromUser`! */
        AxisAlignedBB box = new AxisAlignedBB(getUserEyes).grow(width, width, width).offset(look.scale(distanceFromUser));
        boolean hit = false;
        for (EntityLivingBase entity : user.world.getEntitiesWithinAABB(EntityLivingBase.class, box, e -> e != user)) {
            if (user instanceof EntityPlayer & !ForgeHooks.onPlayerAttackTarget((EntityPlayer) user, entity)) continue;
            float damage;
            if (charge) {
                //damage is based on relative speed between the user and the target
                double relativeSpeed = getSpeed(look, user) - getSpeed(look, entity);
                //non player entities have a way lower speed cap
                if (relativeSpeed <= 4.6 * (user instanceof EntityPlayer ? 1d : 0.2)) continue;
                /* I'm pretty sure Vanilla rounds the Spear Damage up in my testing... */
                //vanilla actually rounds down here for some reason
                damage = (float) Math.floor(relativeSpeed * definition.getChargeMultiplier());
                //charge attacks apparently don't take the mob attack damage attribute into account
            } else damage = definition.getDamage() + (float) user.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            damage += EnchantmentHelper.getModifierForCreature(stack, entity.getCreatureAttribute());
            if (entity.attackEntityFrom(DamageSource.causeMobDamage(user), damage)) hit = true;
            stack.damageItem(1, user);
            if (user instanceof EntityPlayer) ((EntityPlayer) user).addStat(StatList.getObjectUseStats(stack.getItem()));
        }
        if (!charge && user instanceof EntityPlayer) ((EntityPlayer) user).resetCooldown();
        renderHitboxParticles(user, box);
        return hit;
    }

    private static double getSpeed(Vec3d look, Entity entity) {
        //if (entity.isRiding() & !(entity instanceof EntityPlayer)) entity = entity.getLowestRidingEntity();
        double motionX, motionY, motionZ;
        //use our capability for players because players don't actually move in 1.12
        // the server just teleports them to the correct position when it receives a movement packet
        if (entity instanceof EntityPlayer && entity.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null)) {
            CapabilitySpearMovement.ICapabilityMountsPlayerInfo capCharge = entity.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null);
            motionX = entity.posX - capCharge.getPrevX();
            motionY = entity.posY - capCharge.getPrevY();
            motionZ = entity.posZ - capCharge.getPrevZ();
        } else {
            motionX = entity.posX - entity.lastTickPosX;
            motionY = entity.posY - entity.lastTickPosY;
            motionZ = entity.posZ - entity.lastTickPosZ;
        }
        //the look vec is used here to make sure that we're only using the entities speed in the exact direction they are facing
        return look.x * motionX * 20d + look.y * motionY * 20d + look.z * motionZ * 20d;
    }

    //### DEBUG

    /* Garbage code for showing the Spear Attack Bounding Box.*/
    private static void renderHitboxParticles(EntityLivingBase user, AxisAlignedBB box) {
        if (user.world instanceof WorldServer) {
            double x1 = box.minX;
            double y1 = box.minY;
            double z1 = box.minZ;

            double x2 = box.maxX;
            double y2 = box.maxY;
            double z2 = box.maxZ;

            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x1, y1, z1, 1, 0, 0, 0, 0.0D);
            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x1, y1, z2, 1, 0, 0, 0, 0.0D);
            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x1, y2, z1, 1, 0, 0, 0, 0.0D);
            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x1, y2, z2, 1, 0, 0, 0, 0.0D);

            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x2, y1, z1, 1, 0, 0, 0, 0.0D);
            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x2, y1, z2, 1, 0, 0, 0, 0.0D);
            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x2, y2, z1, 1, 0, 0, 0, 0.0D);
            ((WorldServer)user.world).spawnParticle(EnumParticleTypes.CRIT, x2, y2, z2, 1, 0, 0, 0, 0.0D);
        }
    }

}
