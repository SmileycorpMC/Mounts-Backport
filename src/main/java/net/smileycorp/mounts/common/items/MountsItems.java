package net.smileycorp.mounts.common.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.config.SpearRegistry;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class MountsItems {


    public static final ItemJockeySpawner JOCKEY_SPAWNER = new ItemJockeySpawner();
    public static final ItemLeatherHorseArmour LEATHER_HORSE_ARMOUR = new ItemLeatherHorseArmour();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(JOCKEY_SPAWNER);
        registry.register(LEATHER_HORSE_ARMOUR);
        SpearRegistry.getSpears().forEach(registry::register);
    }
}
