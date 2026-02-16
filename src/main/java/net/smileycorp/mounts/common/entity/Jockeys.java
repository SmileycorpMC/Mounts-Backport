package net.smileycorp.mounts.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
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
        world.spawnEntity(living);
        type.spawn(living, true);
        living.playLivingSound();
        return living;
    }

    public static void spawnSpiderJockey(EntityLiving entity, boolean summoned) {
        if (!(entity instanceof EntitySpider)) return;
        DifficultyInstance difficulty = entity.world.getDifficultyForLocation(entity.getPosition());
        if (entity.getClass() != EntitySpider.class) entity.onInitialSpawn(difficulty, null);
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
        skeleton.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0F);
        skeleton.onInitialSpawn(difficulty, null);
        world.spawnEntity(skeleton);
        skeleton.startRiding(entity);
    }

    public static void spawnBabyZombieJockey(EntityLiving entity, boolean summoned) {
        DifficultyInstance difficulty = entity.world.getDifficultyForLocation(entity.getPosition());
        entity.onInitialSpawn(difficulty, null);
        if (summoned) ((EntityZombie)entity).setChild(true);
        entity.tasks.addTask(1, new EntityAIFindMount(entity));
    }

    public static void spawnZombieHorseman(EntityLiving entity, boolean summoned) {
        DifficultyInstance difficulty = entity.world.getDifficultyForLocation(entity.getPosition());
        entity.onInitialSpawn(difficulty, null);
        EntityZombie zombie = new EntityZombie(entity.world);
        zombie.setPosition(entity.posX, entity.posY, entity.posZ);
        zombie.onInitialSpawn(difficulty, null);
        if (zombie.isChild()) ((EntityZombieHorse)entity).setGrowingAge(Integer.MIN_VALUE);
        zombie.startRiding(entity);
        zombie.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(VanillaSpears.IRON_SPEAR.get()));
        zombie.world.spawnEntity(zombie);
    }

    public static void spawnCamelHusk(EntityLiving entity, boolean summoned) {
        World world = entity.world;
        DifficultyInstance difficulty = world.getDifficultyForLocation(entity.getPosition());
        entity.onInitialSpawn(difficulty, null);
        EntityCamelHusk camel = new EntityCamelHusk(world);
        camel.setPosition(entity.posX, entity.posY, entity.posZ);
        camel.onInitialSpawn(difficulty, null);
        entity.startRiding(camel);
        world.spawnEntity(camel);
        EntityParched parched = new EntityParched(world);
        parched.setPosition(entity.posX, entity.posY, entity.posZ);
        parched.onInitialSpawn(difficulty, null);
        parched.startRiding(camel);
        world.spawnEntity(parched);
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(VanillaSpears.IRON_SPEAR.get()));
    }

    public enum Type {
        SPIDER(new ResourceLocation("spider"), Jockeys::spawnSpiderJockey),
        CAVE_SPIDER(new ResourceLocation("cave_spider"), Jockeys::spawnSpiderJockey),
        BABY_ZOMBIE(new ResourceLocation("zombie"), Jockeys::spawnBabyZombieJockey),
        ZOMBIE_HORSEMAN(new ResourceLocation("zombie_horse"), Jockeys::spawnZombieHorseman),
        CAMEL_HUSK(new ResourceLocation("husk"), Jockeys::spawnCamelHusk);

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

        public void spawn(EntityLiving entity, boolean summoned) {
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

        void spawn(EntityLiving entity, boolean summoned);

    }

}
