package net.smileycorp.mounts.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.mounts.common.Constants;

import java.util.function.Supplier;

public class SpearDefinition {

    private final String name;
    private final float damage;
    private final int durability;
    private final int enchantability;
    private final float attackSpeed;
    private final float chargeMultiplier;
    private final int chargeDelay;
    private final int chargeDismountDuration;
    private final float chargeDismountSpeed;
    private final int chargeKnockbackDuration;
    private final float chargeKnockbackSpeed;
    private final int chargeDamageDuration;
    private final float chargeDamageSpeed;
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
        chargeDelay = builder.chargeDelay;
        chargeDismountDuration = builder.chargeDismountDuration;
        chargeDismountSpeed = builder.chargeDismountSpeed;
        chargeKnockbackDuration = builder.chargeKnockbackDuration;
        chargeKnockbackSpeed = builder.chargeKnockbackSpeed;
        chargeDamageDuration = builder.chargeDamageDuration;
        chargeDamageSpeed = builder.chargeDamageSpeed;
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

    public int getChargeDelay() {
        return chargeDelay;
    }

    public int getChargeDismountDuration() {
        return chargeDismountDuration;
    }

    public float getChargeDismountSpeed() {
        return chargeDismountSpeed;
    }

    public int getChargeKnockbackDuration() {
        return chargeKnockbackDuration;
    }

    public float getChargeKnockbackSpeed() {
        return chargeKnockbackSpeed;
    }

    public int getChargeDamageDuration() {
        return chargeDamageDuration;
    }

    public float getChargeDamageSpeed() {
        return chargeDamageSpeed;
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
        if (json.has("charge_delay")) builder.chargeDelay = json.get("charge_delay").getAsInt();
        if (json.has("charge_dismount_duration")) builder.chargeDismountDuration = json.get("charge_dismount_duration").getAsInt();
        if (json.has("charge_dismount_speed")) builder.chargeDismountSpeed = json.get("charge_dismount_speed").getAsFloat();
        if (json.has("charge_knockback_duration")) builder.chargeKnockbackDuration = json.get("charge_knockback_duration").getAsInt();
        if (json.has("charge_knockback_speed")) builder.chargeKnockbackSpeed = json.get("charge_knockback_speed").getAsFloat();
        if (json.has("charge_damage_duration")) builder.chargeDamageDuration = json.get("charge_damage_duration").getAsInt();
        if (json.has("charge_damage_speed")) builder.chargeDamageSpeed = json.get("charge_damage_speed").getAsFloat();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && json.has("creative_tab")) {
            String tabName = json.get("creative_tab").getAsString();
            for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
                if (!tabName.equals(tab.getTabLabel())) continue;
                builder.setCreativeTab(tab);
                break;
            }
        }
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
        private float damage = 1;
        private int durability = 1;
        private int enchantability = 1;
        private float attackSpeed = 1.54f;
        private float chargeMultiplier = 0.7f;
        private int chargeDelay = 15;
        private int chargeDismountDuration = 100;
        private float chargeDismountSpeed = 14;
        private int chargeKnockbackDuration = 200;
        private float chargeKnockbackSpeed = 5.1f;
        private int chargeDamageDuration = 300;
        private float chargeDamageSpeed = 4.6f;
        private CreativeTabs creativeTab = CreativeTabs.COMBAT;
        private Supplier<Ingredient> repairMaterial;
        private boolean craftable = true;
        private boolean fireproof = false;

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

        public Builder setChargeDelay(int chargeDelay) {
            this.chargeDelay = chargeDelay;
            return this;
        }

        public Builder setChargeDismountDuration(int chargeDismountDuration) {
            this.chargeDismountDuration = chargeDismountDuration;
            return this;
        }

        public Builder setChargeDismountSpeed(float chargeDismountSpeed) {
            this.chargeDismountSpeed = chargeDismountSpeed;
            return this;
        }

        public Builder setChargeKnockbackDuration(int chargeKnockbackDuration) {
            this.chargeKnockbackDuration = chargeKnockbackDuration;
            return this;
        }

        public Builder setChargeKnockbackSpeed(float chargeKnockbackSpeed) {
            this.chargeKnockbackSpeed = chargeKnockbackSpeed;
            return this;
        }

        public Builder setChargeDamageDuration(int chargeDamageDuration) {
            this.chargeDamageDuration = chargeDamageDuration;
            return this;
        }

        public Builder setChargeDamageSpeed(float chargeDamageSpeed) {
            this.chargeDamageSpeed = chargeDamageSpeed;
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
