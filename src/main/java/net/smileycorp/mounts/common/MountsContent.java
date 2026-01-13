package net.smileycorp.mounts.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.mounts.api.ItemSpear;
import net.smileycorp.mounts.api.SpearDefinition;
import net.smileycorp.mounts.config.SpearRegistry;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class MountsContent {
    
    private static int ID = 44;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        SpearRegistry.getSpears().forEach(registry::register);
    }
    
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        for (ItemSpear spear : SpearRegistry.getSpears()) {
            SpearDefinition def = spear.getDefinition();
            if (!def.isCraftable()) continue;
            GameRegistry.addShapedRecipe(spear.getRegistryName(), Constants.loc("spear"), new ItemStack(spear),
                    "  M", " S ", "S  ", 'M', def.getRepairMaterial(), 'S', "stickWood");
        }
    }
    
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        MountsSoundEvents.SOUNDS.forEach(registry::register);
    }
    
}
