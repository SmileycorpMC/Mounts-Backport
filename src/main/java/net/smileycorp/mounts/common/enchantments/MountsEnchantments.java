package net.smileycorp.mounts.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.common.Constants;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class MountsEnchantments {

    public static final EnumEnchantmentType SPEAR = EnumHelper.addEnchantmentType("spear", item -> item instanceof ItemSpear);

    public static final Enchantment LUNGE = new EnchantmentLunge();

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        IForgeRegistry<Enchantment> registry = event.getRegistry();
        registry.register(LUNGE);
    }

}
