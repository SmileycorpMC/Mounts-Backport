package net.smileycorp.mounts.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.smileycorp.mounts.api.SpearDefinition;

@Cancelable
public class SpearJabEvent extends LivingEvent {

    private final ItemStack stack;
    private final SpearDefinition definition;

    public SpearJabEvent(EntityLivingBase entity, ItemStack stack, SpearDefinition definition) {
        super(entity);
        this.stack = stack;
        this.definition = definition;
    }

    public ItemStack getStack() {
        return stack;
    }

    public SpearDefinition getSpearDefinition() {
        return definition;
    }

}
