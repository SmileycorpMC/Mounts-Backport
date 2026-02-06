package net.smileycorp.mounts.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.smileycorp.mounts.client.entity.render.RenderParched;
import net.smileycorp.mounts.common.CommonProxy;
import net.smileycorp.mounts.common.Constants;
import net.smileycorp.mounts.common.entity.EntityCamel;
import net.smileycorp.mounts.common.entity.EntityParched;
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

		RenderingRegistry.registerEntityRenderingHandler(EntityCamel.class, RenderCamel::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityParched.class, RenderParched::new);
	}
	
}
