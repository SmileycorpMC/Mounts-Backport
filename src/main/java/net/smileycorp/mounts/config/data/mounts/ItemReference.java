package net.smileycorp.mounts.config.data.mounts;

import com.google.gson.JsonObject;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.config.data.mounts.values.Value;

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

    public boolean matches(SpawnContext ctx) {
        try {
            ItemStack stack = ctx.getEntity().getItemStackFromSlot(EntityEquipmentSlot.fromString(slot.get(ctx)));
            if (!stack.getItem().getRegistryName().toString().equals(item.get(ctx))) return false;
            if (count != null && stack.getCount() != count.get(ctx)) return false;
            if (damage != null && stack.getItemDamage() != stack.getItemDamage()) return false;
            if (nbt != null &! JsonToNBT.getTagFromJson(nbt.get(ctx)).equals(stack.getTagCompound())) return false;
            return true;
        } catch (Exception e) {}
        return false;
    }

    public ItemStack createStack(SpawnContext ctx) {
        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.item.get(ctx)));
            int count = this.count == null ? 1 : this.count.get(ctx);
            int damage = this.damage == null ? 0 : this.damage.get(ctx);
            ItemStack stack = new ItemStack(item, count, damage);
            if (nbt != null) stack.setTagCompound(JsonToNBT.getTagFromJson(nbt.get(ctx)));
            return stack;
        } catch (Exception e) {}
        return ItemStack.EMPTY;
    }

    public void setItem(SpawnContext ctx) {
        try {
            ctx.getEntity().setItemStackToSlot(EntityEquipmentSlot.fromString(slot.get(ctx)), createStack(ctx));
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
