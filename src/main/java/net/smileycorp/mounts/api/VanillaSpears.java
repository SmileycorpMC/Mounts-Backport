package net.smileycorp.mounts.api;

import net.smileycorp.mounts.config.data.SpearRegistry;

import java.util.function.Supplier;

public class VanillaSpears {

    public static final Supplier<ItemSpear> WOODEN_SPEAR = () -> SpearRegistry.INSTANCE.getSpear("wooden");
    public static final Supplier<ItemSpear> STONE_SPEAR  = () -> SpearRegistry.INSTANCE.getSpear("stone");
    public static final Supplier<ItemSpear> COPPER_SPEAR = () -> SpearRegistry.INSTANCE.getSpear("copper");
    public static final Supplier<ItemSpear> IRON_SPEAR = () -> SpearRegistry.INSTANCE.getSpear("iron");
    public static final Supplier<ItemSpear> GOLDEN_SPEAR = () -> SpearRegistry.INSTANCE.getSpear("golden");
    public static final Supplier<ItemSpear> DIAMOND_SPEAR = () -> SpearRegistry.INSTANCE.getSpear("diamond");
    public static final Supplier<ItemSpear> NETHERITE_SPEAR = () -> SpearRegistry.INSTANCE.getSpear("netherite");

}
