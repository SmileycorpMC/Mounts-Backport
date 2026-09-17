package net.smileycorp.mounts.integration;

import net.minecraft.item.Item;
import net.smileycorp.mounts.api.VanillaSpears;
import net.smileycorp.mounts.common.MountsLogger;
import suike.suikecherry.block.ModBlockSmithingTable;

import java.lang.reflect.Field;
import java.util.Map;

public class Cherry112Integration {

    public static void registerNetheriteSpearRecipe() {
        try {
            Field field = ModBlockSmithingTable.class.getDeclaredField("NETHER_RECIPE");
            field.setAccessible(true);
            ((Map<Item, Item>) field.get(null)).put(VanillaSpears.DIAMOND_SPEAR.get(), VanillaSpears.NETHERITE_SPEAR.get());
        } catch (Exception e) {
            MountsLogger.logError("Failed adding cherry 1.12 support recipe", e);
        }
    }

}
