package net.smileycorp.mounts.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.smileycorp.mounts.common.Constants;

import java.util.function.Supplier;

public class SpearDefinition {

    private final String name;
    private final float damage, attackSpeed, chargeMultiplier;
    private final int durability, enchantability;
    private final CreativeTabs creativeTab;
    private Supplier<Ingredient> repairMaterialSupplier;
    private Ingredient repairMaterial;
    private final boolean craftable, fireproof;

    private SpearDefinition(Builder builder) {
        name = builder.name;
        damage = builder.damage;
        durability = builder.durability;
        enchantability = builder.enchantability;
        attackSpeed = builder.attackSpeed;
        chargeMultiplier = builder.chargeMultiplier;
        creativeTab = builder.creativeTab;
        repairMaterialSupplier = builder.repairMaterial;
        craftable = builder.craftable;
        fireproof = builder.fireproof;
    }

    public String getName() {
        return name;
    }

    public float getDamage() {
        return damage;
    }

    public int getDurability() {
        return durability;
    }

    public int getEnchantability() {
        return enchantability;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public float getChargeMultiplier() {
        return chargeMultiplier;
    }

    public CreativeTabs getCreativeTab() {
        return creativeTab;
    }

    public Ingredient getRepairMaterial() {
        if (repairMaterial == null) repairMaterial = repairMaterialSupplier.get();
        return repairMaterial;
    }

    public boolean isCraftable() {
        return getRepairMaterial() != Ingredient.EMPTY && craftable;
    }

    public boolean isFireproof() {
        return fireproof;
    }

    public static SpearDefinition fromJson(String name, JsonObject json) {
        Builder builder = new Builder(name);
        if (json.has("damage")) builder.damage = json.get("damage").getAsFloat();
        if (json.has("durability")) builder.durability = json.get("durability").getAsInt();
        if (json.has("enchantability")) builder.enchantability = json.get("enchantability").getAsInt();
        if (json.has("attack_speed")) builder.attackSpeed= json.get("attack_speed").getAsFloat();
        if (json.has("charge_multiplier")) builder.chargeMultiplier = json.get("charge_multiplier").getAsFloat();
        if (json.has("repair_material")) builder.repairMaterial = SpearDefinition.parseRepairMaterial(json.get("repair_material"));
        if (json.has("craftable")) builder.craftable = json.get("craftable").getAsBoolean();
        if (json.has("fireproof")) builder.fireproof = json.get("fireproof").getAsBoolean();
        return builder.build();
    }

    private static Supplier<Ingredient> parseRepairMaterial(JsonElement json) {
        return () -> {
            try {
                return CraftingHelper.getIngredient(json, new JsonContext(Constants.MODID));
            } catch (Exception e) {
                return Ingredient.EMPTY;
            }
        };
    }

    public static class Builder {

        private final String name;
        private float damage = 1, attackSpeed = 1, chargeMultiplier = 1;
        private int durability = 1, enchantability = 1;
        private CreativeTabs creativeTab = CreativeTabs.COMBAT;
        private Supplier<Ingredient> repairMaterial = null;
        private boolean craftable = true, fireproof = false;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder setDurability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder setEnchantability(int enchantability) {
            this.enchantability = enchantability;
            return this;
        }

        public Builder setAttackSpeed(float attackSpeed) {
            this.attackSpeed = attackSpeed;
            return this;
        }

        public Builder setChargeMultiplier(float chargeMultiplier) {
            this.chargeMultiplier = chargeMultiplier;
            return this;
        }

        public Builder setCreativeTab(CreativeTabs creativeTab) {
            this.creativeTab = creativeTab;
            return this;
        }

        public Builder setRepairMaterial(Supplier<Ingredient> repairMaterial) {
            this.repairMaterial = repairMaterial;
            return this;
        }

        public Builder setCraftable(boolean craftable) {
            this.craftable = craftable;
            return this;
        }

        public Builder setFireproof(boolean fireproof) {
            this.fireproof = fireproof;
            return this;
        }

        public SpearDefinition build() {
            return new SpearDefinition(this);
        }
        
    }

}
