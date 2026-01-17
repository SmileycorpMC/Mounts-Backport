package net.smileycorp.mounts.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.entity.MountsEntities;
import net.smileycorp.mounts.common.network.PacketHandler;

@Mod.EventBusSubscriber
public class CommonProxy
{
	public static DamageSource causeSpearDamage(Entity source)
	{ return (new EntityDamageSource(Constants.MODID + "." + "spear", source)); }

	public void preInit(FMLPreInitializationEvent event)
	{
		MountsEntities.registerEntities();
		MountsEntities.registerEntitySpawns();
		PacketHandler.initPackets();
	}

	public void init(FMLInitializationEvent event)
	{
		CapabilityManager.INSTANCE.register(CapabilitySpearMovement.ICapabilityMountsPlayerInfo.class, new CapabilitySpearMovement.Storage(), CapabilitySpearMovement.MountsPlayerInfoMethods::new);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
	public void serverStart(FMLServerStartingEvent event) {}
	
}
