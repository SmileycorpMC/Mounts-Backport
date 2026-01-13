package net.smileycorp.mounts.api;

import net.smileycorp.mounts.config.SpearRegistry;

import java.util.function.Supplier;

public class VanillaSpears {

    public static final Supplier<ItemSpear> WOODEN_SPEAR = () -> SpearRegistry.getSpear("wooden");
    public static final Supplier<ItemSpear> STONE_SPEAR  = () -> SpearRegistry.getSpear("stone");
    public static final Supplier<ItemSpear> COPPER_SPEAR = () -> SpearRegistry.getSpear("copper");
    public static final Supplier<ItemSpear> IRON_SPEAR = () -> SpearRegistry.getSpear("iron");
    public static final Supplier<ItemSpear> GOLDEN_SPEAR = () -> SpearRegistry.getSpear("golden");
    public static final Supplier<ItemSpear> DIAMOND_SPEAR = () -> SpearRegistry.getSpear("diamond");
    public static final Supplier<ItemSpear> NETHERITE_SPEAR = () -> SpearRegistry.getSpear("netherite");

}
