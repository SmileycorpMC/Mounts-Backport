package net.smileycorp.mounts.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.mounts.common.capabilities.CapabilitySpearMovement;
import net.smileycorp.mounts.common.entity.EntityCamel;
import net.smileycorp.mounts.common.entity.EntityParched;
import net.smileycorp.mounts.common.entity.ai.EntityAIFindMount;
import net.smileycorp.mounts.config.LootConfig;
import net.smileycorp.mounts.config.LootTableEntry;
import net.smileycorp.mounts.config.MountsConfig;
import net.smileycorp.mounts.integration.DeeperDepthsIntegration;

import java.util.Random;
import java.util.Set;

@Mod.EventBusSubscriber
public class MountsCommonEvents
{
    /** When camels are Spawning, deny many of them. */
    @SubscribeEvent
    public static void onCamelSpawn(LivingSpawnEvent.CheckSpawn event)
    {
        if (event.isSpawner()) return;
        if (!(event.getEntityLiving() instanceof EntityCamel)) return;

        /* IDK if this exactly aligns with the `1/13` chance we want for Camels
        (combined /w the current weight of `1/5` that is required before even reaching here)*/
        if (event.getWorld().rand.nextInt(13) >= 5)
        { event.setResult(Event.Result.DENY); }
    }


    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(CapabilitySpearMovement.ID, new CapabilitySpearMovement.Provider(new CapabilitySpearMovement.MountsPlayerInfoMethods(), CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null));
        }
    }

    /** Bonus security check */
    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        if (!player.hasCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null)) return;
        CapabilitySpearMovement.ICapabilityMountsPlayerInfo capCharge = player.getCapability(CapabilitySpearMovement.MOUNTS_PLAYER_CAP, null);
        //reset player previous position so speed doesn't get stored
        capCharge.setPrevPos(player.posX, player.posY, player.posZ);

        //camel code
        if (player.getRidingEntity() instanceof EntityCamel)
        {
            EntityCamel camel = (EntityCamel)player.getRidingEntity();
            if (camel.isSitting() || camel.getDashCooldown() > 0) return;
            if (capCharge.getIsSpaceHeld()) capCharge.setSpaceHeldTime(Math.min(1.0F, capCharge.getSpaceHeldTime() + 0.1F));
        }
    }

    @SubscribeEvent
    public static void addLoot(LootTableLoadEvent event) {
        for (LootTableEntry entry : LootConfig.getLootTableEntries()) if (entry.canApply(event.getName())) {
            entry.addEntry(event.getTable());
            MountsLogger.logInfo("Injected " + entry.getName() + " with weight " + entry.getWeight() + " to pool " + entry.getPool() + " in table " + entry.getLootTable());
        }
    }

    @SubscribeEvent
    public static void spawnMob(LivingSpawnEvent.SpecialSpawn event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityZombie) ((EntityZombie) entity).tasks.addTask(1, new EntityAIFindMount((EntityLiving) entity));
        if (entity.getClass() == EntityCaveSpider.class && entity.world.rand.nextFloat() > MountsConfig.caveSpiderJockeyChance) addRider(entity);
    }

    public static AbstractSkeleton addRider(EntityLivingBase entity) {
        if (!(entity instanceof EntitySpider)) return null;
        World world = entity.world;
        AbstractSkeleton skeleton = new EntitySkeleton(entity.world);
        Random rand = entity.world.rand;
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(entity.world.getBiome(entity.getPosition()));
        if (types.contains(BiomeDictionary.Type.SNOWY) &! types.contains(BiomeDictionary.Type.FOREST)
                && rand.nextFloat() <= MountsConfig.strayChance) skeleton = new EntityStray(world);
        if (types.contains(BiomeDictionary.Type.SANDY) &! (types.contains(BiomeDictionary.Type.FOREST) || types.contains(BiomeDictionary.Type.BEACH))
                && rand.nextFloat() <= MountsConfig.parchedChance) skeleton = new EntityParched(world);
        if (Loader.isModLoaded("deeperdepths") && DeeperDepthsIntegration.canBoggedSpawn(rand, types))
            skeleton = DeeperDepthsIntegration.getBogged(world);
        if (skeleton == null) return null;
        skeleton.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0F);
        skeleton.onInitialSpawn(world.getDifficultyForLocation(entity.getPosition()), null);
        world.spawnEntity(skeleton);
        skeleton.startRiding(entity);
        return skeleton;
    }

}