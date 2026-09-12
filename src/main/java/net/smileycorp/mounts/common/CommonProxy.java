package net.smileycorp.mounts.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.smileycorp.mounts.client.particle.MountsParticle;
import net.smileycorp.mounts.common.advancements.MountsAdvancements;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearAnimation;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.capabilities.Piercing;
import net.smileycorp.mounts.common.entity.EntityParched;
import net.smileycorp.mounts.common.entity.MountsEntities;
import net.smileycorp.mounts.common.network.PacketHandler;
import net.smileycorp.mounts.common.network.SpearSpawnParticleMessage;
import net.smileycorp.mounts.config.EntityConfig;
import net.smileycorp.mounts.config.GeneralConfig;
import net.smileycorp.mounts.config.MountsConfig;
import net.smileycorp.mounts.config.data.LootRegistry;
import net.smileycorp.mounts.config.data.SpearRegistry;
import net.smileycorp.mounts.config.data.mobs.DataRegistry;
import net.smileycorp.mounts.config.data.mobs.MobDataLoader;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class CommonProxy
{
	public static DamageSource causeSpearDamage(Entity source)
	{ return (new EntityDamageSource(Constants.MODID + "." + "spear", source)); }

	public void preInit(FMLPreInitializationEvent event)
	{
		GeneralConfig.syncConfig(event);
		EntityConfig.syncConfig(event);
		MountsConfig.syncConfig(event);
		DataRegistry.init();
		SpearRegistry.init(event);
		LootRegistry.init(event);
		MobDataLoader.init(event);
		MountsEntities.registerEntities();
		MountsEntities.registerEntitySpawns();
		MountsLootTables.registerLootTables();
		MountsAdvancements.registerCriterionTriggers();
		PacketHandler.initPackets();
	}

	public void init(FMLInitializationEvent event)
	{
		CapabilityManager.INSTANCE.register(CapabilitySpearMovement.ICapabilityMountsPlayerInfo.class, new CapabilitySpearMovement.Storage(), CapabilitySpearMovement.MountsPlayerInfoMethods::new);
		CapabilityManager.INSTANCE.register(Piercing.class, Piercing.STORAGE, Piercing.Impl::new);
		CapabilityManager.INSTANCE.register(CapabilitySpearAnimation.ICapabilityAnimations.class, new CapabilitySpearAnimation.Storage(), CapabilitySpearAnimation.AnimationMethods::new);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		//add parched spawns
		//doing this in postInit, so we don't have to iterate through the list every time a mob spawns in a desert in using events
		MountsLogger.blankLine();
		MountsLogger.heading("ADDING PARCHED SPAWNS");
		for (Biome biome : ForgeRegistries.BIOMES) {
			Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
			//mesas are also counted as sandy biomes so we check for them here
			//beaches have their own type but if a mod doesn't set their beaches up correctly they will flag as sandy
			if (!types.contains(BiomeDictionary.Type.SANDY) || types.contains(BiomeDictionary.Type.MESA)
					|| types.contains(BiomeDictionary.Type.BEACH)) continue;
			List<Biome.SpawnListEntry> entries = biome.getSpawnableList(EnumCreatureType.MONSTER);
			Biome.SpawnListEntry skeleton = null;
			for (Biome.SpawnListEntry entry : entries)
				if (entry.entityClass == EntitySkeleton.class) {
					skeleton = entry;
					break;
				}
			if (skeleton == null) continue;
			entries.remove(skeleton);
			int weight = skeleton.itemWeight / 2;
			MountsLogger.logInfo("Adding Parched and Skeleton spawns to " + biome.getRegistryName() + ", with weight: " + weight + ", minGroup: " + skeleton.minGroupCount + ", max group: " + skeleton.maxGroupCount);
			entries.add(new Biome.SpawnListEntry(EntitySkeleton.class, weight, skeleton.minGroupCount, skeleton.maxGroupCount));
			entries.add(new Biome.SpawnListEntry(EntityParched.class, weight, skeleton.minGroupCount, skeleton.maxGroupCount));
		}
		MobDataLoader.INSTANCE.loadData();
	}
	
	public void serverStart(FMLServerStartingEvent event) {}

	/**
	 *  Specialized particle method that sends particles on servers
	 * */
	public void spawnParticle(MountsParticle particle, World world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
	{
		if (world.isRemote)
		{ spawnParticle(particle, posX, posY, posZ, speedX, speedY, speedZ, parameters); }
		else
		{ PacketHandler.NETWORK_INSTANCE.sendToAllTracking( new SpearSpawnParticleMessage(particle.getId(), posX, posY, posZ, speedX, speedY, speedZ, parameters), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 0.0D)); }
	}

	public void spawnParticle(MountsParticle particle, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters) {}
}
