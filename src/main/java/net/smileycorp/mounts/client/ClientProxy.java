package net.smileycorp.mounts.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.mounts.client.entity.render.RenderCamel;
import net.smileycorp.mounts.client.entity.render.RenderCamelHusk;
import net.smileycorp.mounts.client.entity.render.RenderParched;
import net.smileycorp.mounts.common.CommonProxy;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.MountsContent;
import net.smileycorp.mounts.common.entity.EntityCamel;
import net.smileycorp.mounts.common.entity.EntityCamelHusk;
import net.smileycorp.mounts.common.entity.EntityParched;
import net.smileycorp.mounts.common.entity.Jockeys;
import net.smileycorp.mounts.common.items.ItemJockeySpawner;
import net.smileycorp.mounts.config.SpearRegistry;

@EventBusSubscriber(value = Side.CLIENT, modid= Constants.MODID)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		SpearRegistry.getSpears().forEach(spear -> ModelLoader.setCustomModelResourceLocation(spear, 0,
				new ModelResourceLocation(spear.getRegistryName(), "normal")));
		for (int i = 0; i < Jockeys.Type.values().length; i++) ModelLoader.setCustomModelResourceLocation(MountsContent.JOCKEY_SPAWNER, i,
				new ModelResourceLocation("spawn_egg"));
		RenderingRegistry.registerEntityRenderingHandler(EntityCamel.class, RenderCamel::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCamelHusk.class, RenderCamelHusk::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityParched.class, RenderParched::new);
	}

	//colour our custom spawn egg
	@SubscribeEvent
	public static void itemColourHandler(ColorHandlerEvent.Item event) {
		ItemColors registry = event.getItemColors();
		registry.registerItemColorHandler(ItemJockeySpawner::getColours, MountsContent.JOCKEY_SPAWNER);
	}
	
}
