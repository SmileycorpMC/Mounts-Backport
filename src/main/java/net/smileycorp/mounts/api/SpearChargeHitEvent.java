package net.smileycorp.mounts.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.smileycorp.mounts.common.capabilities.Piercing;

public class SpearChargeHitEvent extends LivingEvent {

    private final ItemStack stack;
    private final SpearDefinition definition;
    private final Entity target;

    public SpearChargeHitEvent(EntityLivingBase entity, Entity target, ItemStack stack, SpearDefinition definition) {
        super(entity);
        this.target = target;
        this.stack = stack;
        this.definition = definition;
    }

    public ItemStack getStack() {
        return stack;
    }

    public SpearDefinition getSpearDefinition() {
        return definition;
    }

    public Entity getTarget() {
        return target;
    }

    public Piercing getPiercingCapability() {
        return getEntity().getCapability(Piercing.CAPABILITY, null);
    }

    @Cancelable
    public static class Pre extends SpearChargeHitEvent {

        public Pre(EntityLivingBase entity, Entity target, ItemStack stack, SpearDefinition definition) {
            super(entity, target, stack, definition);
        }

    }

    @Cancelable
    public static class Post extends SpearChargeHitEvent {

        public Post(EntityLivingBase entity, Entity target, ItemStack stack, SpearDefinition definition) {
            super(entity, target, stack, definition);
        }

    }

}
