package net.smileycorp.mounts.integration;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import wornhorseshoes.util.IHorseStackGetter;

public class WornHorseshoesIntegration {

    public static ItemStack getHorseArmor(AbstractHorse horse) {
        return IHorseStackGetter.getArmorStack(horse);
    }

}
