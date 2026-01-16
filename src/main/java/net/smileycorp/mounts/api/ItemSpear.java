package net.smileycorp.mounts.api;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
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

    /*@Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        /* Replace w/ an override later, probably use Forge's PlayerInteract Events? A Capability will be needed, but it wouldn't be hard. */
    /*performSpearAttack(attacker);

        stack.damageItem(1, attacker);
        return super.hitEntity(stack, target, attacker);
    }*/

    public static boolean performSpearAttack(EntityLivingBase user, boolean charge) {
        ItemStack stack = user.getHeldItemMainhand();
        if (user.world.isRemote |! (stack.getItem() instanceof ItemSpear)) return false;
        SpearDefinition definition = ((ItemSpear) stack.getItem()).getDefinition();
        Vec3d look = user.getLookVec();
        BlockPos getUserEyes = user.getPosition().add(new BlockPos(0, user.getEyeHeight(), 0));
        /* Controls the distance the attack box is shifted away from the user. */
        double distanceFromUser = 4.0D;
        double width = 0.25D;

        /* Make a sized Bounding Box, and push it forward by `distanceFromUser`! */
        AxisAlignedBB box = new AxisAlignedBB(getUserEyes).grow(width, width, width).offset(look.scale(distanceFromUser));

        for (EntityLivingBase entity : user.world.getEntitiesWithinAABB(EntityLivingBase.class, box, e -> e != user))
        {
            if (user instanceof EntityPlayer &! ForgeHooks.onPlayerAttackTarget((EntityPlayer) user, entity)) continue;
            float damage = definition.getDamage();
            if (charge) {
                /* Give the delicious damage right here???*/
                //apparently the damage done by spears is the player speed minus the target speed so this needs to be down here
                double speedMPS = MathHelper.sqrt(user.motionX * user.motionX + user.motionY * user.motionY + user.motionZ * user.motionZ)
                        - MathHelper.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ);
                /* I'm pretty sure Vanilla rounds the Spear Damage up in my testing... */
                damage = (float)Math.ceil(speedMPS * definition.getChargeMultiplier());
            }
            damage = calculateDamageAlterations(damage, user, entity);
            entity.attackEntityFrom(DamageSource.causeMobDamage(user), damage);
            stack.damageItem(1, user);
            if (user instanceof EntityPlayer) ((EntityPlayer) user).addStat(StatList.getObjectUseStats(stack.getItem()));
        }
        if (!charge && user instanceof EntityPlayer) ((EntityPlayer) user).resetCooldown();

        /* Garbage code for showing the Spear Attack Bounding Box.*/
        if (user.world instanceof WorldServer)
        {
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
        return true;
    }

    //I did not steal this from the deeper depths mace, nope
    public static float calculateDamageAlterations(float damage, EntityLivingBase attacker, Entity target)
    {
        float f = (float)attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        float f1;

        if (target instanceof EntityLivingBase)
        { f1 = EnchantmentHelper.getModifierForCreature(attacker.getHeldItemMainhand(), ((EntityLivingBase) target).getCreatureAttribute()); }
        else
        { f1 = EnchantmentHelper.getModifierForCreature(attacker.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED); }

        return damage + f + f1;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        performSpearAttack(playerIn, true);
        return new ActionResult(EnumActionResult.PASS, playerIn.getHeldItem(hand));
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
}
