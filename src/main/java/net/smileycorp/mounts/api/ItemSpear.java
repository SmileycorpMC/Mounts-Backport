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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.MountsSoundEvents;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.capabilities.Piercing;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

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
        return definition.getChargeDelay() + Math.max(definition.getChargeDismountDuration(),
                Math.max(definition.getChargeKnockbackDuration(), definition.getChargeDamageDuration()));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        player.world.playSound(player, player.posX, player.posY, player.posZ, ((ItemSpear) player.getHeldItem(hand).getItem()).getUseSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        performChargeAttack(player, stack);
        super.onUsingTick(stack, player, count);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase user, int timeLeft) {
        super.onPlayerStoppedUsing(stack, world, user, timeLeft);
        if (user.hasCapability(Piercing.CAPABILITY, null)) user.getCapability(Piercing.CAPABILITY, null).clear();
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

    public SoundEvent getAttackSound() { return MountsSoundEvents.ITEM_SPEAR_ATTACK; }
    public SoundEvent getHitSound() { return MountsSoundEvents.ITEM_SPEAR_HIT; }
    public SoundEvent getUseSound() { return MountsSoundEvents.ITEM_SPEAR_USE; }

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

    public static boolean performJabAttack(EntityLivingBase user, ItemStack stack) {
        if (user.world.isRemote |! (stack.getItem() instanceof ItemSpear)) return false;
        SpearDefinition definition = ((ItemSpear) stack.getItem()).getDefinition();
        boolean hit = false;
        for (Entity entity : getHitEntities(user, e -> true)) {
            /* I'm pretty sure Vanilla rounds the Spear Damage up in my testing... */
            //vanilla actually rounds down here for some reason
            float damage = definition.getDamage() + (float) user.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            if (damage <= 0) continue;
            if (entity instanceof EntityLivingBase) damage += EnchantmentHelper.getModifierForCreature(stack, ((EntityLivingBase) entity).getCreatureAttribute());
            if (entity.attackEntityFrom(DamageSource.causeMobDamage(user), damage)) hit = true;
            else continue;
            stack.damageItem(1, user);
            if (user instanceof EntityPlayer) {
                ((EntityPlayer) user).addStat(StatList.getObjectUseStats(stack.getItem()));
                ((EntityPlayer) user).resetCooldown();
            }
        }
        user.world.playSound(null, user.posX, user.posY, user.posZ, hit ? ((ItemSpear) stack.getItem()).getHitSound()
                : ((ItemSpear) stack.getItem()).getAttackSound(), user.getSoundCategory(), 1, 1);
        return hit;
    }

    //there are so many charged checks here I'm just gonna separate the methods
    public static boolean performChargeAttack(EntityLivingBase user, ItemStack stack) {
        if (user.world.isRemote |! (stack.getItem() instanceof ItemSpear)) return false;
        SpearDefinition definition = ((ItemSpear) stack.getItem()).getDefinition();
        int usageTicks = user.getItemInUseMaxCount() - definition.getChargeDelay();
        if (usageTicks < 0) return false;
        if (!user.hasCapability(Piercing.CAPABILITY, null)) return false;
        Piercing piercing = user.getCapability(Piercing.CAPABILITY, null);
        Vec3d look = user.getLookVec();
        double speed = getSpeed(look, user);
        boolean hit = false;
        for (Entity entity : getHitEntities(user, e -> piercing.canPierce(e))) {
            boolean pierced = false;
            //damage is based on relative speed between the user and the target
            double relativeSpeed = speed - getSpeed(look, entity);
            if (speed >= definition.getChargeDismountSpeed() && usageTicks <= definition.getChargeDismountDuration() && entity.isRiding()) {
                entity.dismountRidingEntity();
                pierced = true;
            }
            if (entity instanceof EntityLivingBase && speed >= definition.getChargeKnockbackSpeed() && usageTicks <= definition.getChargeKnockbackDuration()) {
                ((EntityLivingBase) entity).knockBack(user, 0.4f + EnchantmentHelper.getKnockbackModifier(user) / 2f,
                        MathHelper.sin(user.rotationYaw * 0.017453292f), -MathHelper.cos(user.rotationYaw * 0.017453292f));
                pierced = true;
            }
            //charge attacks apparently don't take the mob attack damage attribute into account
            //non player entities have a way lower speed cap
            float damage = relativeSpeed <= definition.getChargeDamageSpeed() * (user instanceof EntityPlayer ? 1d : 0.2) ?
                    0 : (float) Math.floor(relativeSpeed * definition.getChargeMultiplier());
            if (damage > 0) {
                if (entity instanceof EntityLivingBase) damage += EnchantmentHelper.getModifierForCreature(stack, ((EntityLivingBase) entity).getCreatureAttribute());
                if (entity.attackEntityFrom(DamageSource.causeMobDamage(user), damage)) pierced = true;
            }
            if (!pierced) continue;
            stack.damageItem(1, user);
            if (user instanceof EntityPlayer) ((EntityPlayer) user).addStat(StatList.getObjectUseStats(stack.getItem()));
            piercing.pierce(entity);
            hit = true;
        }
        if (user instanceof EntityPlayer && piercing.getPiercedEntities().size() > 5); //advancement trigger
        if (hit) user.world.playSound(null, user.posX, user.posY, user.posZ, ((ItemSpear) stack.getItem()).getHitSound(), user.getSoundCategory(), 1, 1);
        return hit;
    }

    public static List<Entity> getHitEntities(EntityLivingBase user, Predicate<Entity> predicate) {
        BlockPos getUserEyes = user.getPosition().add(new BlockPos(0, user.getEyeHeight(), 0));
        /* Controls the distance the attack box is shifted away from the user. */
        double distanceFromUser = user instanceof EntityPlayer ? 4 : 2;
        double width = 0.25D;

        /* Make a sized Bounding Box, and push it forward by `distanceFromUser`! */
        AxisAlignedBB box = new AxisAlignedBB(getUserEyes).grow(width, width, width).offset(user.getLookVec().scale(distanceFromUser));
        renderHitboxParticles(user, box);
        return user.world.getEntitiesWithinAABB(EntityLivingBase.class, box, entity -> entity != user && entity.isEntityAlive() &!
                (user instanceof EntityPlayer &! ForgeHooks.onPlayerAttackTarget((EntityPlayer) user, entity)) && predicate.test(entity));
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
