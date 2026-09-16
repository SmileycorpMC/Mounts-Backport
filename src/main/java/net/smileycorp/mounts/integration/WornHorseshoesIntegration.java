package net.smileycorp.mounts.integration;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wornhorseshoes.item.ItemHorseArmor;
import wornhorseshoes.util.IHorseStackGetter;

public class WornHorseshoesIntegration {

    public static ItemStack getHorseArmor(AbstractHorse horse) {
        return IHorseStackGetter.getArmorStack(horse);
    }

    public static boolean isHorseArmour(Item item) {
        return item instanceof ItemHorseArmor;
    }

}
