package net.smileycorp.mounts.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Loader;
import net.smileycorp.mounts.api.VanillaSpears;
import net.smileycorp.mounts.common.entity.ai.EntityAIFindMount;
import net.smileycorp.mounts.config.MountsConfig;
import net.smileycorp.mounts.integration.DeeperDepthsIntegration;

import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Jockeys {

    public static EntityLiving spawn(World world, Type type, double x, double y, double z) {
        if (type == null) return null;
        Entity entity = EntityList.createEntityByIDFromName(type.entity, world);
        if (!(entity instanceof EntityLiving)) return null;
        EntityLiving living = (EntityLiving) entity;
        living.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360f), 0);
        living.rotationYawHead = living.rotationYaw;
        living.renderYawOffset = living.rotationYaw;
        living.setPosition(x, y, z);
        living.onInitialSpawn(living.world.getDifficultyForLocation(living.getPosition()), null);
        world.spawnEntity(living);
        type.spawn(living, true);
        living.playLivingSound();
        return living;
    }

    public static void spawnSpiderJockey(EntityLivingBase entity, boolean summoned) {
        if (!(entity instanceof EntitySpider)) return;
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
        if (skeleton == null) return;
        skeleton.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0F);
        skeleton.onInitialSpawn(world.getDifficultyForLocation(entity.getPosition()), null);
        world.spawnEntity(skeleton);
        skeleton.startRiding(entity);
    }

    public static void spawnBabyZombieJockey(EntityLivingBase entity, boolean summoned) {
        if (summoned) ((EntityZombie)entity).setChild(true);
        ((EntityZombie) entity).tasks.addTask(1, new EntityAIFindMount((EntityLiving) entity));
    }

    public static void spawnZombieHorseman(EntityLivingBase entity, boolean summoned) {
        EntityZombie zombie = new EntityZombie(entity.world);
        zombie.setPosition(entity.posX, entity.posY, entity.posZ);
        zombie.onInitialSpawn(entity.world.getDifficultyForLocation(entity.getPosition()), null);
        zombie.startRiding(entity, true);
        zombie.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, VanillaSpears.IRON_SPEAR.get().getDefaultInstance());
        zombie.world.spawnEntity(zombie);
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, VanillaSpears.IRON_SPEAR.get().getDefaultInstance());
    }

    public static void spawnCamelHusk(EntityLivingBase entity, boolean summoned) {
        World world = entity.world;
        DifficultyInstance difficulty = world.getDifficultyForLocation(entity.getPosition());
        EntityCamelHusk camel = new EntityCamelHusk(world);
        camel.setPosition(entity.posX, entity.posY, entity.posZ);
        camel.onInitialSpawn(difficulty, null);
        entity.startRiding(camel, true);
        world.spawnEntity(camel);
        EntityParched parched = new EntityParched(world);
        parched.setPosition(entity.posX, entity.posY, entity.posZ);
        parched.onInitialSpawn(difficulty, null);
        parched.startRiding(camel, true);
        world.spawnEntity(parched);
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, VanillaSpears.IRON_SPEAR.get().getDefaultInstance());
    }


    public enum Type {
        SPIDER(new ResourceLocation("spider"), Jockeys::spawnSpiderJockey),
        CAVE_SPIDER(new ResourceLocation("cave_spider"), Jockeys::spawnSpiderJockey),
        BABY_ZOMBIE(new ResourceLocation("zombie"), Jockeys::spawnBabyZombieJockey),
        ZOMBIE_HORSEMAN(new ResourceLocation("zombie_horse"), Jockeys::spawnZombieHorseman),
        HUSK_CAMEL(new ResourceLocation("husk"), Jockeys::spawnCamelHusk);

        private final ResourceLocation entity;
        private final Spawner spawner;
        private final String name;

        Type(ResourceLocation entity, Spawner spawner) {
            this.entity = entity;
            this.spawner = spawner;
            this.name = "jockey.mounts." + name().toLowerCase(Locale.US);
        }

        public ResourceLocation getEntity() {
            return entity;
        }

        public void spawn(EntityLivingBase entity, boolean summoned) {
            spawner.spawn(entity, summoned);
        }

        public String getUnlocalizedName() {
            return name;
        }

        public static Type get(int id) {
            return id >= values().length ? null : values()[id];
        }

    }

    @FunctionalInterface
    public interface Spawner {

        void spawn(EntityLivingBase entity, boolean summoned);

    }

}
