package net.smileycorp.mounts.api;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
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

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return super.hitEntity(stack, target, attacker);
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
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return true;
    }

}
