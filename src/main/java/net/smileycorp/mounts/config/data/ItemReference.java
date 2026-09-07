package net.smileycorp.mounts.config.data;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.config.data.values.Value;

public class ItemReference {

    private final Value<String> item, slot, nbt;
    private final Value<Integer> count, damage;

    private ItemReference(Builder builder) {
        this.item = builder.item;
        this.slot = builder.slot;
        this.count = builder.count;
        this.damage = builder.damage;
        this.nbt = builder.nbt;
    }

    public boolean matches(EntityLiving entity) {
        try {
            ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.fromString(slot.get(entity)));
            if (!stack.getItem().getRegistryName().toString().equals(item.get(entity))) return false;
            if (count != null && stack.getCount() != count.get(entity)) return false;
            if (damage != null && stack.getItemDamage() != stack.getItemDamage()) return false;
            if (nbt != null &! JsonToNBT.getTagFromJson(nbt.get(entity)).equals(stack.getTagCompound())) return false;
            return true;
        } catch (Exception e) {}
        return false;
    }

    public ItemStack createStack(EntityLiving entity) {
        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.item.get(entity)));
            int count = this.count == null ? 1 : this.count.get(entity);
            int damage = this.damage == null ? 0 : this.damage.get(entity);
            ItemStack stack = new ItemStack(item, count, damage);
            if (nbt != null) stack.setTagCompound(JsonToNBT.getTagFromJson(nbt.get(entity)));
            return stack;
        } catch (Exception e) {}
        return ItemStack.EMPTY;
    }

    public void setItem(EntityLiving entity) {
        try {
            entity.setItemStackToSlot(EntityEquipmentSlot.fromString(slot.get(entity)), createStack(entity));
        } catch (Exception e) {}
    }

    public static ItemReference fromJson(JsonObject obj) throws Exception {
        Builder builder = Builder.of(DataRegistry.readValue(DataType.STRING, obj.get("item")),
                DataRegistry.readValue(DataType.STRING, obj.get("slot")));
        if (obj.has("count")) builder.count = DataRegistry.readValue(DataType.INT, obj.get("count"));
        if (obj.has("damage")) builder.damage = DataRegistry.readValue(DataType.INT, obj.get("damage"));
        if (obj.has("nbt")) builder.nbt = DataRegistry.readValue(DataType.STRING, obj.get("nbt"));
        return builder.build();
    }

    public static class Builder {

        private final Value<String> item, slot;
        private Value<Integer> count, damage = null;
        private Value<String> nbt = null;

        private Builder(Value<String> item, Value<String> slot) {
            this.item = item;
            this.slot = slot;
        }

        public Builder count(Value<Integer> count) {
            this.count = count;
            return this;
        }

        public Builder damage(Value<Integer> damage) {
            this.damage = damage;
            return this;
        }

        public Builder nbt(Value<String> nbt) {
            this.nbt = nbt;
            return this;
        }

        public ItemReference build() {
            return new ItemReference(this);
        }

        public static Builder of(Value<String> item, Value<String> slot) {
            return new Builder(item, slot);
        }

    }


}
